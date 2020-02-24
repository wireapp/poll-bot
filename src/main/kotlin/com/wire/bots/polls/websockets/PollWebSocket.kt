package com.wire.bots.polls.websockets

import com.wire.bots.polls.dto.messages.UsersMessage
import com.wire.bots.polls.services.PollService
import com.wire.bots.polls.utils.createJson
import io.ktor.client.HttpClient
import io.ktor.http.cio.websocket.send
import mu.KLogging

class PollWebSocket(private val client: HttpClient, private val config: WebSocketConfig, private val pollService: PollService) {

    private companion object : KLogging()

    suspend fun run() = client.createJsonWebSocketReceiver<UsersMessage>(config) { session, message ->
        val poll = pollService.createPoll(message)
        session.send(createJson(poll ?: "It was not possible to create poll!"))
    }.subscribe()
}
