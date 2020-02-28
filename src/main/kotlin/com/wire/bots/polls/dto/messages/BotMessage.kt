package com.wire.bots.polls.dto.messages

/**
 * Common interface for the messages sent to proxy from the bot.
 */
interface BotMessage {
    /**
     * Mandatory type of the message.
     */
    val type: String
}
