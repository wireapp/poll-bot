package com.wire.bots.polls.websockets

import com.wire.bots.polls.dto.messages.Message
import com.wire.bots.polls.services.MessagesHandlingService
import io.ktor.client.HttpClient
import mu.KLogging

class PollWebSocket(private val client: HttpClient, private val config: WebSocketConfig, private val handler: MessagesHandlingService) {

    private companion object : KLogging()

    suspend fun run() = client.createJsonWebSocketReceiver<Message>(config) { _, message ->
        handler.handle(message)
    }.subscribe()
}
