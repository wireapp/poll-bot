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
            else -> tokenAwareHandle(
                requireNotNull(message.token) { "The reply from bot is expected, message must contain token." },
                message
            )
        }

        logger.debug { "Message handled." }
    }

    private suspend fun tokenAwareHandle(token: String, message: Message) {
        when (message.type) {
            "conversation.init" -> {
                logger.debug { "Init message received." }
                greetingsService.sayHello(message)
            }
            "conversation.new_text" -> {
                logger.debug { "New text message received." }
                handleText(token, message)
            }
            "conversation.new_image" -> logger.debug { "New image posted to conversation, ignoring." }
            "conversation.reaction" -> {
                logger.debug { "Reaction message" }
                pollService.sendStats(
                    token = token,
                    pollId = requireNotNull(message.refMessageId) { "Reaction must contain refMessageId" }
                )
            }
            "conversation.poll.action" -> {
                val poll = requireNotNull(message.poll) { "Reaction to a poll, poll object must be set!" }
                pollService.pollAction(
                    token,
                    PollAction(
                        pollId = poll.id,
                        optionId = requireNotNull(poll.offset) { "Offset/Option id must be set!" },
                        userId = requireNotNull(message.userId) { "UserId of user who sent the message must be set." }
                    )
                )
            }
            else -> logger.warn { "Unknown message type of ${message.type}. Ignoring." }
        }
    }

    private suspend fun handleText(token: String, message: Message) {
        with(message) {
            when {
                userId == null -> throw IllegalArgumentException("UserId must be set for text messages.")
                // it is a reply on something
                refMessageId != null -> when {
                    // like of the message, maybe the poll -> send stats if the poll exist
                    reaction != null -> pollService.sendStats(token, refMessageId)
                    // it contains text
                    text != null -> when {
                        // request for stats
                        text.trim().startsWith("/stats") -> pollService.sendStats(token, refMessageId)
                        // integer vote where the text contains offset
                        text.trim().toIntOrNull() != null -> vote(token, userId, refMessageId, text)
                        else -> logger.info { "Ignoring the message as it is reply unrelated to the bot" }
                    }
                    else -> logger.info { "Ignoring the message as it has set refMessageId but both text and reaction are null." }
                }
                // text message with just text
                text != null -> {
                    when {
                        // poll request
                        text.trim().startsWith("/poll") -> pollService.createPoll(token, UsersInput(userId, text))
                        else -> logger.info { "Ignoring the message, unrecognized command." }
                    }
                }
                else -> logger.info { "Ignoring message as it does not have correct fields set." }
            }
        }
    }

    private suspend fun vote(token: String, userId: String, refMessageId: String, text: String) = pollService.pollAction(
        token,
        PollAction(
            pollId = refMessageId,
            optionId = requireNotNull(text.toIntOrNull()) { "Text message must be a valid integer." },
            userId = userId
        )
    )
}
