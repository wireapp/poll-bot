package com.wire.bots.polls.filters

import com.wire.bots.polls.dto.messages.Message

interface RequestFilter<T> {
    suspend fun filter(toFilter: T)
}

interface UsersMessageFilter : RequestFilter<Message>
