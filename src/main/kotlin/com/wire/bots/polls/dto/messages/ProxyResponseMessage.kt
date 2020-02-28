package com.wire.bots.polls.dto.messages

/**
 * Respond received from the proxy to every message from the bot.
 */
data class ProxyResponseMessage(
    /**
     * ID of the message bot sent.
     */
    val messageId: String
)
