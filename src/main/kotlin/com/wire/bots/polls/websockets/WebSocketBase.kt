package com.wire.bots.polls.websockets

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.ws
import io.ktor.http.DEFAULT_PORT
import io.ktor.http.cio.websocket.Frame
import mu.KLogging

abstract class WebSocketBase(protected val client: HttpClient, protected val config: WebSocketConfig) {

    private companion object : KLogging()

    suspend fun subscribe() {
        // TODO websocket reconnect
        client.ws(
            host = config.host,
            port = config.port ?: DEFAULT_PORT,
            path = config.path
        ) {
            for (frame in incoming) {
                logger.info { "received" }
                runCatching { onFrameReceived(frame) }
                    .onFailure { logger.error(it) { "Exception occurred during handling onFrameReceived." } }

            }
            logger.info { "Closing the socket" }
        }

    }

    abstract suspend fun DefaultClientWebSocketSession.onFrameReceived(frame: Frame)
}
