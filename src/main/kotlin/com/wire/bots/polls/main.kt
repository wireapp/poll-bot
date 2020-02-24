package com.wire.bots.polls

import com.wire.bots.polls.routing.registerRoutes
import com.wire.bots.polls.setup.installFrameworks
import com.wire.bots.polls.setup.setupKodein
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

data class A(val b: String, val c: Int)

fun main() {

    val server = embeddedServer(Netty, 8080) {
        installFrameworks()
        setupKodein()

        routing {
            registerRoutes()
        }
    }
    server.start(wait = true)
}
