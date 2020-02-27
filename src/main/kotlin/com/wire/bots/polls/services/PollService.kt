package com.wire.bots.polls.services

import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.bots.polls.dto.PollAction
import com.wire.bots.polls.dto.PollDto
import com.wire.bots.polls.dto.UsersInput
import com.wire.bots.polls.dto.messages.PollActionConfirmationMessage
import com.wire.bots.polls.dto.messages.TextMessage
import com.wire.bots.polls.dto.toProxyMessage
import com.wire.bots.polls.parser.PollFactory
import mu.KLogging
import java.util.UUID

class PollService(private val factory: PollFactory, private val proxySenderService: ProxySenderService) {

    private companion object : KLogging()

    suspend fun createPoll(token: String, usersInput: UsersInput): PollDto? {
        val poll = factory.forUserInput(usersInput)
            .whenNull { logger.warn { "It was not possible to create poll." } } ?: return null

        // TODO save to the database
        val id = UUID.randomUUID()
        val response = proxySenderService.send(token, poll.toProxyMessage(id))
        logger.info { "Poll successfully created with id: ${response.messageId}" }
        return poll
    }

    suspend fun pollAction(token: String, pollAction: PollAction) {
        logger.info { "User voted" }
        val response = proxySenderService.send(
            token, PollActionConfirmationMessage(
                poll = PollActionConfirmationMessage.Poll(
                    id = pollAction.pollId,
                    offset = pollAction.optionId,
                    userId = pollAction.userId
                )
            )
        )
        logger.info { "Proxy received confirmation for vote under id: ${response.messageId}" }
    }

    suspend fun sendStats(token: String, usersInput: UsersInput) {
        // TODO fetch stats from db
        proxySenderService.send(token, TextMessage("no stats yet"))
    }
}
