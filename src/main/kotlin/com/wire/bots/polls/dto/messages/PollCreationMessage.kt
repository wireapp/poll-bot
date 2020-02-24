package com.wire.bots.polls.dto.messages

data class PollCreationMessage(
    val type: String,
    val poll: PollMessage
)

data class PollMessage(
    val body: String,
    val buttons: List<String>
)
