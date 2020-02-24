package com.wire.bots.polls.filters

import com.wire.bots.polls.dto.messages.UsersMessage

interface RequestFilter<T> {
    suspend fun filter(toFilter: T)
}

interface UsersMessageFilter : RequestFilter<UsersMessage>
