package com.wire.bots.polls.dto

import com.wire.bots.polls.dto.messages.PollCreationMessage
import java.util.UUID

typealias Question = String
typealias Option = String

data class PollDto(
    val question: Question,
    val options: List<Option>
)

/**
 * Converts poll to the message for the proxy.
 */
fun PollDto.toProxyMessage(id: UUID) = PollCreationMessage(
    poll = PollCreationMessage.Poll(
        id = id.toString(),
        body = this.question,
        buttons = options
    )
)
