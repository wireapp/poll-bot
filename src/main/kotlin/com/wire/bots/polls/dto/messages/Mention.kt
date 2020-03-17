package com.wire.bots.polls.dto.messages

data class Mention(
    val userId: String,
    val offset: Int,
    val length: Int
)
