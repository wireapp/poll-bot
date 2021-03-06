package com.wire.bots.polls.dto

import com.wire.bots.polls.dto.common.Mention

/**
 * Wrapper for the text received by this bot. Should be used as a container for all user texts in the bot.
 *
 * This is in order not to log sensitive text to the log.
 */
data class UsersInput(
    /**
     * Id of the user who wrote this.
     */
    val userId: String,
    /**
     * User's text, not logged.
     */
    val input: String,
    /**
     * Mentions
     */
    val mentions: List<Mention>
) {
    //TODO modify this in the future - because we do not want to print decrypted users text to the log
    override fun toString(): String = "User: $userId wrote $input"
}
