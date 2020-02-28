package com.wire.bots.polls.dto.messages

/**
 * This message creates a poll.
 */
data class PollCreationMessage(
    /**
     * Poll object.
     */
    val poll: Poll,
    override val type: String = "poll.new"
) : BotMessage {

    /**
     * Poll representation for the proxy.
     */
    data class Poll(
        /**
         * ID of the poll
         */
        val id: String,
        /**
         * Question asked.
         */
        val body: String,
        /**
         * Ordered options.
         */
        val buttons: List<String>
    )
}
