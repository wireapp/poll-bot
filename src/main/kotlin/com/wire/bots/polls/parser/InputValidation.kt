package com.wire.bots.polls.parser

import com.wire.bots.polls.dto.UsersInput

class InputValidation {

    fun shouldAccept(userInput: UsersInput) = userInput.input.trim().startsWith("/poll")

    fun <T> accepting(userInput: UsersInput, block: (UsersInput) -> T): T? = if (shouldAccept(userInput)) block(userInput) else null
}
