package com.wire.bots.polls.websockets

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.ws
import io.ktor.http.cio.websocket.Frame

abstract class WebSocketBase(protected val client: HttpClient, protected val config: WebSocketConfig) {

    suspend fun subscribe() {
        client.ws(
            host = config.host,
            port = config.port, path = config.path
        ) {
            for (frame in incoming) {
                onFrameReceive(frame)
            }
        }

    }

    abstract suspend fun DefaultClientWebSocketSession.onFrameReceive(frame: Frame)
}
