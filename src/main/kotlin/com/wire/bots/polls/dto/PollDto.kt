package com.wire.bots.polls.dto

import com.wire.bots.polls.dto.common.Mention

typealias Option = String

data class PollDto(
    val question: Question,
    val options: List<Option>
)

data class Question(
    val body: String,
    val mentions: List<Mention>
)
