package com.wire.bots.polls.services

import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.bots.polls.dao.PollOptions
import com.wire.bots.polls.dao.Polls
import com.wire.bots.polls.dao.Votes
import com.wire.bots.polls.dto.PollAction
import com.wire.bots.polls.dto.PollDto
import com.wire.bots.polls.dto.UsersInput
import com.wire.bots.polls.dto.messages.PollActionConfirmationMessage
import com.wire.bots.polls.dto.messages.TextMessage
import com.wire.bots.polls.dto.toProxyMessage
import com.wire.bots.polls.parser.PollFactory
import mu.KLogging
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

/**
 * Service handling the polls. It communicates with the proxy via [proxySenderService].
 */
class PollService(private val factory: PollFactory, private val proxySenderService: ProxySenderService) {

    private companion object : KLogging()

    /**
     * Create poll.
     */
    suspend fun createPoll(token: String, usersInput: UsersInput): PollDto? {
        val poll = factory.forUserInput(usersInput)
            .whenNull { logger.warn { "It was not possible to create poll." } } ?: return null

        // TODO save to the database
        val pollId = UUID.randomUUID()

        newSuspendedTransaction {
            Polls.insert {
                it[id] = pollId.toString()
                it[ownerId] = usersInput.userId
                it[isActive] = true
                it[body] = poll.question
            }

            poll.options.forEachIndexed { index, option ->
                PollOptions.insert {
                    it[this.pollId] = pollId.toString()
                    it[optionOrder] = index
                    it[optionContent] = option
                }
            }
        }

        val response = proxySenderService.send(token, poll.toProxyMessage(pollId))
        logger.info { "Poll successfully created with id: ${response.messageId}" }
        return poll
    }

    /**
     * Record that the user voted.
     */
    suspend fun pollAction(token: String, pollAction: PollAction) {
        logger.info { "User voted" }

        newSuspendedTransaction {
            val voteId = PollOptions.select {
                (PollOptions.pollId eq pollAction.pollId) and (PollOptions.optionOrder eq pollAction.optionId)
            }.singleOrNull()
                ?.getOrNull(PollOptions.id)
                ?.value
                .whenNull {
                    logger.error { "There is no poll which has id ${pollAction.pollId} or no option with order ${pollAction.optionId}" }
                } ?: throw IllegalArgumentException("No poll or option found for the received vote!")


            Votes.insert {
                it[pollOption] = voteId
                it[userId] = pollAction.userId
            }
        }

        val response = proxySenderService.send(
            token = token,
            message = PollActionConfirmationMessage(
                poll = PollActionConfirmationMessage.Poll(
                    id = pollAction.pollId,
                    offset = pollAction.optionId,
                    userId = pollAction.userId
                )
            )
        )
        logger.info { "Proxy received confirmation for vote under id: ${response.messageId}" }
    }

    /**
     * Sends statistics about the poll to the proxy.
     */
    suspend fun sendStats(token: String, @Suppress("UNUSED_PARAMETER") usersInput: UsersInput) {
        // TODO get the poll id sent by proxy
        val pollId = "<some id>"

        val result = newSuspendedTransaction {
            (PollOptions leftJoin Votes)
                .slice(PollOptions.optionOrder, Votes.userId)
                .select {
                    (PollOptions.pollId eq pollId) and Votes.userId.isNotNull()
                }.groupBy({ it[PollOptions.optionOrder] }, { it[Votes.userId] })
        }

        val text = result.map { (optionId, votingUsers) ->
            "$optionId - ${votingUsers.joinToString(", ")}"
        }.joinToString(System.lineSeparator())

        proxySenderService.send(
            token, TextMessage(
                """
                    Results for pollId: $pollId
                    $text
                """.trimIndent()
            )
        )
    }
}
