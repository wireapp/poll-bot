package com.wire.bots.polls.routing

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get

/**
 * Register routes to the KTor.
 */
fun Routing.registerRoutes() {

    get("/") {
        call.respond("This is the Wire Poll Bot.")
    }

    healthStatus()
    messages()
}
