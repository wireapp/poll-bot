package com.wire.bots.polls.integration_tests.dto

/**
 * Configuration saying where does the bot run.
 */
data class BotApiConfiguration(
    /**
     * URL of the bot eg. localhost:8080
     */
    val baseUrl: String,
    /**
     * Token for bearer auth.
     */
    val token: String
)
