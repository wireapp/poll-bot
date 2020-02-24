package com.wire.bots.polls.websockets

import com.wire.bots.polls.dto.messages.UsersMessage
import com.wire.bots.polls.services.PollService
import io.ktor.client.HttpClient
import mu.KLogging

class PollWebSocket(private val client: HttpClient, private val config: WebSocketConfig, private val pollService: PollService) {

    private companion object : KLogging()

    suspend fun run() = client.createJsonWebSocketReceiver<UsersMessage>(config) { _, message ->
        pollService.createPoll(message)
    }.subscribe()
}
