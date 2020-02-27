package com.wire.bots.polls.dto.messages

data class TextMessage(
    val text: String,
    override val type: String = "text"
) : BotMessage
