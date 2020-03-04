package com.wire.bots.polls.integration_tests.dto

/**
 * Respond received from the proxy to every message from the bot.
 */
data class ProxyResponseMessage(
    /**
     * ID of the message bot sent.
     */
    val messageId: String
)
