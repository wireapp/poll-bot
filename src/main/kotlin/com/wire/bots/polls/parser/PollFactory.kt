package com.wire.bots.polls.parser

import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.bots.polls.dto.PollDto
import com.wire.bots.polls.dto.UsersInput
import mu.KLogging

class PollFactory(private val inputParser: InputParser, private val pollValidation: PollValidation) {

    private companion object : KLogging()

    // TODO create better error handling and return probably Either
    fun forUserInput(usersInput: UsersInput): PollDto? {
        val poll = inputParser.parsePoll(usersInput).whenNull {
            logger.warn { "It was not possible to create poll for user input $usersInput." }
        } ?: return null

        val (valid, errors) = pollValidation.validate(poll)
        return if (valid) {
            logger.debug { "Poll successfully created for user input: $usersInput" }
            poll
        } else {
            logger.warn {
                "It was not possible to create poll for user input: $usersInput due to errors listed bellow:" +
                        "${System.lineSeparator()}${errors.joinToString(System.lineSeparator())}"
            }
            null
        }
    }
}
