package com.wire.bots.polls.services

import com.wire.bots.polls.dto.bot.fallBackMessage
import com.wire.bots.polls.dto.bot.greeting
import com.wire.bots.polls.dto.roman.Message
import mu.KLogging

/**
 * Service used for handling init message.
 */
class UserCommunicationService(private val proxySenderService: ProxySenderService) {

    private companion object : KLogging() {
        const val usage = "To create poll please text: `/poll \"Question\" \"Option 1\" \"Option 2\"`."
    }

    /**
     * Sends hello message with instructions to the conversation.
     */
    suspend fun sayHello(message: Message) {
        proxySenderService.send(
            requireNotNull(message.token) { "Token can not be null!" },
            greeting(
                text = "Hello, I'm Poll Bot. $usage"
            )
        )
    }

    suspend fun reactionToWrongCommand(token: String) {
        proxySenderService.send(
            token,
            message = fallBackMessage(
                text = "I couldn't recognize your command. $usage"
            )
        )
    }

}
