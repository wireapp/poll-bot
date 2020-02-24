package com.wire.bots.polls.dto

typealias Question = String
typealias Option = String


data class PollDto(
    val question: Question,
    val options: List<Option>
)
