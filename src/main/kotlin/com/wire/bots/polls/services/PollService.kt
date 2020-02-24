package com.wire.bots.polls.services

import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.bots.polls.dto.PollDto
import com.wire.bots.polls.dto.UsersInput
import com.wire.bots.polls.dto.messages.UsersMessage
import com.wire.bots.polls.dto.toProxyMessage
import com.wire.bots.polls.parser.PollFactory
import mu.KLogging

class PollService(private val factory: PollFactory, private val proxySenderService: ProxySenderService) {

    private companion object : KLogging()

    // TODO replace with db fetch
    private val defaultToken = ""

    suspend fun createPoll(usersMessage: UsersMessage): PollDto? {
        val poll = usersMessage.text
            .whenNull { logger.error { "Text field is null even though poll sequence was expected." } }
            ?.let { factory.forUserInput(UsersInput(it)) }
            .whenNull { logger.warn { "It was not possible to create poll." } } ?: return null

        // TODO save to the database

        proxySenderService.sendPoll(usersMessage.token ?: defaultToken, poll.toProxyMessage())

        return poll
    }
}
