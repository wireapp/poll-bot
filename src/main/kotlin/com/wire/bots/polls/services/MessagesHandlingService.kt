package com.wire.bots.polls.services

import com.wire.bots.polls.dto.PollAction
import com.wire.bots.polls.dto.UsersInput
import com.wire.bots.polls.dto.messages.Message
import mu.KLogging

class MessagesHandlingService(private val pollService: PollService) {

    private companion object : KLogging()

    suspend fun handle(message: Message) {
        logger.info { "Handling message." }

        when (message.type) {
            "conversation.bot_request" -> logger.info { "Bot was added to conversation." }
            // TODO store token to the db
            "conversation.init" -> logger.info { "Init message received." }
            "conversation.new_text" -> handleText(requireNotNull(message.token), message)
            "conversation.new_image" -> logger.info { "New image posted to conversation, ignoring." }
            // TODO add better error handling
            "conversation.poll.action" -> {
                val poll = requireNotNull(message.poll)

                pollService.pollAction(
                    requireNotNull(message.token), PollAction(
                        pollId = poll.id,
                        optionId = requireNotNull(poll.offset),
                        userId = requireNotNull(poll.userId)
                    )
                )
            }
            else -> logger.warn { "Unknown message type of ${message.type}. Ignoring." }
        }
        logger.info { "Message handled." }
    }

    private suspend fun handleText(token: String, message: Message) {
        val userInput = UsersInput(requireNotNull(message.text))
        with(userInput.input) {
            when {
                startsWith("/poll") -> {
                    pollService.createPoll(token, userInput)
                }
                startsWith("/stats") -> {
                    pollService.sendStats(token, userInput)
                }
                else -> logger.info { "Ignoring text, unrecognized command: $userInput" }
            }
        }
    }
}
