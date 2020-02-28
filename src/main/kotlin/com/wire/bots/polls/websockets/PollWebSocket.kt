package com.wire.bots.polls.websockets

import com.wire.bots.polls.dto.messages.Message
import com.wire.bots.polls.services.MessagesHandlingService
import io.ktor.client.HttpClient
import mu.KLogging

/**
 * Class which contains logic for receiving JSON formatted request from the given [WebSocketConfig].
 */
class PollWebSocket(private val client: HttpClient, private val config: WebSocketConfig, private val handler: MessagesHandlingService) {

    private companion object : KLogging()

    /**
     * Starts listening on the web socket.
     */
    suspend fun run() = client.createJsonWebSocketReceiver<Message>(config) { _, message ->
        handler.handle(message)
    }.subscribe()
}
