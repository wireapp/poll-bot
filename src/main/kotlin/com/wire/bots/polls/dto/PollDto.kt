package com.wire.bots.polls.dto

import com.wire.bots.polls.dto.messages.PollCreationMessage
import com.wire.bots.polls.dto.messages.PollMessage

typealias Question = String
typealias Option = String

data class PollDto(
    val question: Question,
    val options: List<Option>
)

fun PollDto.toProxyMessage() = PollCreationMessage(
    type = "poll",
    poll = PollMessage(
        body = this.question,
        buttons = options
    )
)
