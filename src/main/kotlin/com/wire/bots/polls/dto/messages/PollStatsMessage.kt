package com.wire.bots.polls.dto.messages

data class PollStatsMessage(
    val text: String,
    override val type: String = "text"
) : BotMessage
