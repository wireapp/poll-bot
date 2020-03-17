package com.wire.bots.polls.dto.bot

import com.wire.bots.polls.dto.common.Text

internal data class TextMessage(
    val text: Text,
    override val type: String = "text"
) : BotMessage
