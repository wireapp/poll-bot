package com.wire.bots.polls.websockets

import io.ktor.application.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein

fun Application.subscribeToWebSockets() {
    val kodein by kodein()

    GlobalScope.launch(Dispatchers.IO) {
        val pollWebSocket by kodein.instance<PollWebSocket>()
        pollWebSocket.run()
    }
}
