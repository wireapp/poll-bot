package com.wire.bots.polls.routing

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.registerRoutes() {
    get("/") {
        call.respond("Hello from the bot.")
    }

    messages()
}
