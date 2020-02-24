package com.wire.bots.polls.parser

import com.wire.bots.polls.dto.PollDto
import com.wire.bots.polls.dto.UsersInput
import mu.KLogging

class InputParser(private val validation: InputValidation) {

    private companion object : KLogging()

    fun parsePoll(userInput: UsersInput): PollDto? {
        if (!validation.shouldAccept(userInput)) {
            logger.info { "Input string $userInput wont be processed because it does not contain poll starting sequence /poll." }
            return null
        }

        logger.debug { "/poll input found, creating poll" }
        return createPoll(userInput)
    }

    private fun createPoll(userInput: UsersInput): PollDto? {
        //TODO currently not supporting char " in the strings
        val inputs = userInput.input
            .substringAfter("/poll")
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
