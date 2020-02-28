package com.wire.bots.polls.dto

/**
 * Wrapper for the text received by this bot. Should be used as a container for all user texts in the bot.
 *
 * This is in order not to log sensitive text to the log.
 */
data class UsersInput(
    /**
     * User's text, not logged.
     */
    val input: String
) {
    //TODO modify this in the future - because we do not want to print decrypted users text to the log
    override fun toString(): String = input
}
