package com.wire.bots.polls.websockets

import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.bots.polls.utils.jacksonMapper
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import mu.KLogging
import kotlin.reflect.KClass

open class WebSocketJsonReceiver<T : Any>(
    client: HttpClient,
    config: WebSocketConfig,
    private val clazz: KClass<T>,
    private val onJsonReceived: (suspend (DefaultClientWebSocketSession, T) -> Unit)?
) : WebSocketBase(client, config) {

    private companion object : KLogging()

    override suspend fun DefaultClientWebSocketSession.onFrameReceived(frame: Frame) {
        when (frame) {
            is Frame.Text -> {
                logger.debug { "Text frame received." }
                val text = frame.readText()
                logger.info { "Received text:\n$text" }
                @Suppress("BlockingMethodInNonBlockingContext")
                jacksonMapper().readValue<T>(text, clazz.java)
                    .whenNull { logger.error { "It was not possible to parse incoming message!" } }
                    ?.let { onJsonReceived(it) }
            }
            else -> logger.debug { "Received non-text frame, not processing it." }
        }
    }

    open suspend fun DefaultClientWebSocketSession.onJsonReceived(payload: T) =
        onJsonReceived?.invoke(this, payload) ?: logger.warn { "No action specified, skipping." }
}


inline fun <reified T : Any> HttpClient.createJsonWebSocketReceiver(
    config: WebSocketConfig,
    noinline onJsonReceived: suspend (DefaultClientWebSocketSession, T) -> Unit
) = WebSocketJsonReceiver(this, config, T::class, onJsonReceived)
