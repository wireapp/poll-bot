package com.wire.bots.polls.websockets

import io.ktor.application.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein

/**
 * Start listening the preconfigured web sockets.
 */
fun Application.subscribeToWebSockets() {
    val k by kodein()

    GlobalScope.launch(Dispatchers.IO) {
        val pollWebSocket by k.instance<PollWebSocket>()
        pollWebSocket.run()
    }
}
