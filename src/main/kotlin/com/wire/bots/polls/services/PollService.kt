package com.wire.bots.polls.services

import ai.blindspot.ktoolz.extensions.newLine
import ai.blindspot.ktoolz.extensions.whenNull
import ai.blindspot.ktoolz.extensions.whenTrue
import com.wire.bots.polls.dao.PollRepository
import com.wire.bots.polls.dto.PollAction
import com.wire.bots.polls.dto.UsersInput
import com.wire.bots.polls.dto.bot.confirmVote
import com.wire.bots.polls.dto.bot.newPoll
import com.wire.bots.polls.dto.bot.statsMessage
import com.wire.bots.polls.dto.common.Mention
import com.wire.bots.polls.parser.PollFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KLogging
import java.util.UUID

/**
 * Service handling the polls. It communicates with the proxy via [proxySenderService].
 */
class PollService(
    private val factory: PollFactory,
    private val proxySenderService: ProxySenderService,
    private val repository: PollRepository,
    private val conversationService: ConversationService,
    private val userCommunicationService: UserCommunicationService
) {

    private companion object : KLogging()

    /**
     * Create poll.
     */
    suspend fun createPoll(token: String, usersInput: UsersInput): UUID? {
        val poll = factory.forUserInput(usersInput)
            .whenNull {
                logger.warn { "It was not possible to create poll." }
                pollNotParsedFallback(token, usersInput)
            } ?: return null

        val pollId = repository.savePoll(poll, pollId = UUID.randomUUID(), userId = usersInput.userId)
        logger.info { "Poll successfully created with id: $pollId" }

        // send response with async way
        GlobalScope.launch {
            val response = proxySenderService.send(
                token,
                message = newPoll(
                    id = pollId.toString(),
                    body = poll.question,
                    buttons = poll.options,
                    mentions = shiftMentions(usersInput)
                )
            )
            logger.info { "Poll successfully created with id: ${response.messageId}" }
        }

        return pollId
    }

    private fun shiftMentions(usersInput: UsersInput): List<Mention> {
        val questionBeginningIdx = usersInput.input.indexOfFirst { it == '"' } + 1
        val emptyCharsInQuestion = usersInput.input.substringAfter('"')
            .takeWhile { it == ' ' }
            .count()

        val offsetChange = questionBeginningIdx + emptyCharsInQuestion
        return usersInput.mentions.map { mention ->
            mention.copy(offset = mention.offset - offsetChange)
        }
    }

    private suspend fun pollNotParsedFallback(token: String, usersInput: UsersInput) {
        usersInput.input.startsWith("/poll").whenTrue {
            GlobalScope.launch {
                logger.info { "Command started with /poll, sending usage to user." }
                userCommunicationService.reactionToWrongCommand(token)
            }
        }

    }

    /**
     * Record that the user voted.
     */
    suspend fun pollAction(token: String, pollAction: PollAction) {
        logger.info { "User voted" }
        repository.vote(pollAction)
        logger.info { "Vote registered." }

        GlobalScope.launch {
            val response = proxySenderService.send(
                token = token,
                message = confirmVote(
                    pollId = pollAction.pollId,
                    offset = pollAction.optionId,
                    userId = pollAction.userId
                )
            )
            logger.info { "Proxy received confirmation for vote under id: ${response.messageId}" }

            sendStatsIfAllVoted(token, pollAction.pollId)
        }
    }

    private suspend fun sendStatsIfAllVoted(token: String, pollId: String) {
        val conversationMembersCount = conversationService.getNumberOfConversationMembers(token)
            .whenNull { logger.warn { "It was not possible to determine number of conversation members!" } } ?: return

        val votedSize = repository.votingUsers(pollId).size

        if (votedSize == conversationMembersCount) {
            logger.info { "All users voted, sending statistics to the conversation." }
            sendStats(token, pollId)
        } else {
            logger.info { "Users voted: $votedSize, members of conversation: $conversationMembersCount" }
        }
    }

    /**
     * Sends statistics about the poll to the proxy.
     */
    suspend fun sendStats(token: String, pollId: String) {
        val result = repository.stats(pollId)

        if (result.isEmpty()) {
            logger.info { "There are no data for given pollId." }
            return
        }

        val text = result.map { (optionId, votingUsers) ->
            "$optionId - ${votingUsers.size} ${if (votingUsers.size == 1) "vote" else "votes"}"
        }.joinToString(newLine)

        GlobalScope.launch {
            proxySenderService.send(
                token, statsMessage(
                    text = "Results for pollId: `$pollId`$newLine```$newLine$text$newLine```"
                )
            )
        }
    }
}
