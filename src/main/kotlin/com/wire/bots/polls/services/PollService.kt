package com.wire.bots.polls.services

import ai.blindspot.ktoolz.extensions.whenNull
import ai.blindspot.ktoolz.extensions.whenTrue
import com.wire.bots.polls.dao.PollRepository
import com.wire.bots.polls.dto.PollAction
import com.wire.bots.polls.dto.UsersInput
import com.wire.bots.polls.dto.bot.confirmVote
import com.wire.bots.polls.dto.bot.newPoll
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
    private val userCommunicationService: UserCommunicationService,
    private val statsFormattingService: StatsFormattingService
) {

    private companion object : KLogging()

    /**
     * Create poll.
     */
    suspend fun createPoll(token: String, usersInput: UsersInput): String? {
        val poll = factory.forUserInput(usersInput)
            .whenNull {
                logger.warn { "It was not possible to create poll." }
                pollNotParsedFallback(token, usersInput)
            } ?: return null

        val pollId = repository.savePoll(poll, pollId = UUID.randomUUID().toString(), userId = usersInput.userId)
        logger.info { "Poll successfully created with id: $pollId" }

        // send response with async way
        GlobalScope.launch {
            proxySenderService.send(
                token,
                message = newPoll(
                    id = pollId,
                    body = poll.question.body,
                    buttons = poll.options,
                    mentions = poll.question.mentions
                )
            ).whenNull {
                logger.error { "It was not possible to send the poll to the Roman!" }
            }?.also { (messageId) ->
                logger.info { "Poll successfully created with id: $messageId" }
            }
        }

        return pollId
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
            proxySenderService.send(
                token = token,
                message = confirmVote(
                    pollId = pollAction.pollId,
                    offset = pollAction.optionId,
                    userId = pollAction.userId
                )
            ).whenNull {
                logger.error { "It was not possible to send response to vote." }
            }?.also { (messageId) ->
                logger.info { "Proxy received confirmation for vote under id: $messageId" }
                sendStatsIfAllVoted(token, pollAction.pollId)
            }
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
        val stats = statsFormattingService.formatStats(pollId) ?: return
        GlobalScope.launch { proxySenderService.send(token, stats) }
    }

    /**
     * Sends stats for latest poll.
     */
    suspend fun sendStatsForLatest(token: String) {
        val latest = repository.getNewestPoll().whenNull {
            logger.warn { "No polls found!" }
        } ?: return

        sendStats(token, latest)
    }
}
