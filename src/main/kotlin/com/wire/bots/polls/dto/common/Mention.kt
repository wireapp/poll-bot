package com.wire.bots.polls.dto.common

data class Mention(
    val userId: String,
    val offset: Int,
    val length: Int
)
