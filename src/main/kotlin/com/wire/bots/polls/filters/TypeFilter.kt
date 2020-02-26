package com.wire.bots.polls.filters

import com.wire.bots.polls.dto.messages.Message
import com.wire.bots.polls.exceptions.NotSupportedTypeException
import mu.KLogging

class TypeFilter : UsersMessageFilter {

    private companion object : KLogging() {
        val supportedTypes = setOf("text", "poll")
    }

    override suspend fun filter(toFilter: Message) {
        // simple validation, only receive type text and type poll
        if (!supportedTypes.contains(toFilter.type)) {
            logger.warn { "Leaving message $toFilter, unsupported type ${toFilter.type}" }
            throw NotSupportedTypeException(toFilter.type, supportedTypes)
        }
    }
}
