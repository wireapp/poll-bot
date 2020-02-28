package com.wire.bots.polls.websockets

/**
 * Configuration for [PollWebSocket] class.
 */
data class WebSocketConfig(
    /**
     * Host address - ie. proxy.services.zinfra.io
     */
    val host: String,
    /**
     * Path to socket - ie. /await/ws
     */
    val path: String,
    /**
     * Port, if null, default is used.
     */
    val port: Int? = null
)
