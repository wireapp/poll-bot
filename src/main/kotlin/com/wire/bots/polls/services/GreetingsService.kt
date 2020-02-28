package com.wire.bots.polls.services

import com.wire.bots.polls.dto.messages.Message
import com.wire.bots.polls.dto.messages.TextMessage

/**
 * Service used for handling init message.
 */
class GreetingsService(private val proxySenderService: ProxySenderService) {

    /**
     * Sends hello message with instructions to the conversation.
     */
    suspend fun sayHello(message: Message) {
        proxySenderService.send(
            requireNotNull(message.token) { "Token can not be null!" },
            TextMessage(
                text = "To create poll please text: /poll \"Question\" \"Option 1\" \"Option 2\""
            )
        )
    }
}
