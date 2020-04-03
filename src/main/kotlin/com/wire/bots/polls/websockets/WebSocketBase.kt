package com.wire.bots.polls.websockets

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.ws
import io.ktor.http.DEFAULT_PORT
import io.ktor.http.cio.websocket.Frame
import mu.KLogging

/**
 * Base class for web socket connections.
 */
abstract class WebSocketBase(private val client: HttpClient, private val config: WebSocketConfig) {

    private companion object : KLogging()

    /**
     * Opens web socket and starts receiving connections. [keepAlive] determines whether the app should try to reconnect when the
     * connection is closed.
     */
    tailrec suspend fun subscribe(keepAlive: Boolean = true) {
        // TODO websocket reconnect - use better solution than dummy while true
        runCatching {
            client.ws(
                host = config.host,
                port = config.port ?: DEFAULT_PORT,
                path = config.path
            ) {
                for (frame in incoming) {
                    logger.debug { "WS frame received." }
                    runCatching { onFrameReceived(frame) }
                        .onFailure { logger.error(it) { "Exception occurred during handling onFrameReceived." } }

                }
                logger.info { "Closing the socket" }
            }
        }.onFailure {
            logger.error(it) { "Exception occurred while receiving web sockets. Keep Alive - $keepAlive" }
        }
        // use tail recursion if the bot should keep the connection opened
        if (keepAlive) subscribe(keepAlive)
    }

    /**
     * Method called when frame is received.
     */
    abstract suspend fun DefaultClientWebSocketSession.onFrameReceived(frame: Frame)
}
