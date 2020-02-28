package com.wire.bots.polls.services

import com.wire.bots.polls.dto.PollAction
import com.wire.bots.polls.dto.UsersInput
import com.wire.bots.polls.dto.messages.Message
import mu.KLogging

class MessagesHandlingService(
    private val pollService: PollService,
    private val greetingsService: GreetingsService
) {

    private companion object : KLogging()

    suspend fun handle(message: Message) {
        logger.debug { "Handling message." }

        when (message.type) {
            "conversation.bot_request" -> logger.info { "Bot was added to conversation." }
            "conversation.init" -> {
                logger.debug { "Init message received." }
                greetingsService.sayHello(message)
            }
            "conversation.new_text" -> {
                logger.debug { "New text message received." }
                handleText(requireNotNull(message.token) { "Token must not be null!" }, message)
            }
            "conversation.new_image" -> logger.debug { "New image posted to conversation, ignoring." }
            // TODO add better error handling
            "conversation.poll.action" -> {
                val poll = requireNotNull(message.poll) { "Reaction to a poll, poll object must be set!" }

                pollService.pollAction(
                    requireNotNull(message.token) { "Token must not be null!" },
                    PollAction(
                        pollId = poll.id,
                        optionId = requireNotNull(poll.offset),
                        userId = requireNotNull(poll.userId)
                    )
                )
            }
            else -> logger.warn { "Unknown message type of ${message.type}. Ignoring." }
        }

        logger.debug { "Message handled." }
    }

    private suspend fun handleText(token: String, message: Message) {
        val userInput = UsersInput(requireNotNull(message.text) { "Text message received, text field can not be null!" })
        with(userInput.input) {
            when {
                startsWith("/poll") -> pollService.createPoll(token, userInput)
                startsWith("/stats") -> pollService.sendStats(token, userInput)
                else -> logger.info { "Ignoring text, unrecognized command: $userInput" }
            }
        }
    }
}
