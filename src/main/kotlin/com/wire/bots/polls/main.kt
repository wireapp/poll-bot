package com.wire.bots.polls

import com.wire.bots.polls.routing.registerRoutes
import com.wire.bots.polls.setup.installFrameworks
import com.wire.bots.polls.setup.setupKodein
import com.wire.bots.polls.websockets.subscribeToWebSockets
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
fun main() {

    val server = embeddedServer(Netty, 8080) {
        setupKodein()
        installFrameworks()

        subscribeToWebSockets()

        routing {
            registerRoutes()
        }
    }
    server.start(wait = true)
}
