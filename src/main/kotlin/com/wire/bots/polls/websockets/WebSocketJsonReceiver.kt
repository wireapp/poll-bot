package com.wire.bots.polls.websockets

import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.bots.polls.utils.jacksonMapper
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import mu.KLogging
import kotlin.reflect.KClass

/**
 * Class which uses JSON parser for [Frame.Text].
 */
open class WebSocketJsonReceiver<T : Any>(
    client: HttpClient,
    config: WebSocketConfig,
    private val clazz: KClass<T>,
    private val onJsonReceived: (suspend (DefaultClientWebSocketSession, T) -> Unit)?
) : WebSocketBase(client, config) {

    private companion object : KLogging()

    /**
     * Default implementation of on received which parses the JSON from [frame].
     *
     * Note that there is no error handling and parse exceptions will be propagated.
     */
    override suspend fun DefaultClientWebSocketSession.onFrameReceived(frame: Frame) {
        when (frame) {
            is Frame.Text -> {
                logger.debug { "Text frame received." }
                val text = frame.readText()
                // TODO remove this when going to prod as it prints users data to the log
                logger.info { "Received text:\n$text" }

                @Suppress("BlockingMethodInNonBlockingContext") // because sadly jackson does not have async read
                jacksonMapper().readValue<T>(text, clazz.java)
                    .whenNull { logger.error { "It was not possible to parse incoming message!" } }
                    ?.let { onJsonReceived(it) }
            }
            else -> logger.debug { "Received non-text frame, not processing it." }
        }
    }

    /**
     * Default implementation for the handling of received JSONs. [onJsonReceived] is used when provided.
     */
    open suspend fun DefaultClientWebSocketSession.onJsonReceived(payload: T) =
        onJsonReceived?.invoke(this, payload) ?: logger.warn { "No action specified, skipping." }
}

/**
 * Create instance of [WebSocketJsonReceiver] with [onJsonReceived] for handling the JSONs.
 */
inline fun <reified T : Any> HttpClient.createJsonWebSocketReceiver(
    config: WebSocketConfig,
    noinline onJsonReceived: suspend (DefaultClientWebSocketSession, T) -> Unit
) = WebSocketJsonReceiver(this, config, T::class, onJsonReceived)
