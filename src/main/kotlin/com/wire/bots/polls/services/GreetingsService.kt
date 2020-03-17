package com.wire.bots.polls.services

import com.wire.bots.polls.dto.bot.greeting
import com.wire.bots.polls.dto.roman.Message

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
            greeting(
                text = "To create poll please text: /poll \"Question\" \"Option 1\" \"Option 2\""
            )
        )
    }
}
