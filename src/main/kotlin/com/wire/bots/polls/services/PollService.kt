package com.wire.bots.polls.services

import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.bots.polls.dto.UsersInput
import com.wire.bots.polls.dto.messages.UsersMessage
import com.wire.bots.polls.parser.PollFactory
import mu.KLogging

class PollService(private val factory: PollFactory) {

    private companion object : KLogging()

    suspend fun createPoll(usersMessage: UsersMessage) = usersMessage.text
        ?.whenNull { logger.error { "Text field is null even though poll sequence was expected." } }
        ?.let { factory.forUserInput(UsersInput(it)) }
    // TODO save to the database
}
