package com.wire.bots.polls.parser

import com.wire.bots.polls.dto.PollDto
import com.wire.bots.polls.dto.UsersInput
import mu.KLogging

class InputParser {

    private companion object : KLogging()

    fun parsePoll(userInput: UsersInput): PollDto? {
        //TODO currently not supporting char " in the strings
        val inputs = userInput.input
            .substringAfter("/poll", "")
            .split("\"")
            .filter { it.isNotBlank() }
            .map { it.trim() }

        if (inputs.isEmpty()) {
            logger.warn { "Given user input does not contain valid poll." }
            return null
        }

        return PollDto(
            question = inputs.first(),
            options = inputs.takeLast(inputs.size - 1)
        )
    }
}
