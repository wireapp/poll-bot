package com.wire.bots.polls.dto.messages

data class PollCreationMessage(
    val poll: Poll,
    override val type: String = "poll.new"
) : BotMessage {

    data class Poll(
        val id: String,
        val body: String,
        val buttons: List<String>
    )
}
