package com.wire.bots.polls.integration_tests.routing

import com.wire.bots.polls.integration_tests.dto.Conversation
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post

/**
 * Register routes to the KTor.
 */
fun Routing.registerRoutes() {

    get("/") {
        call.respond("Integration test is up and running.")
    }

    post("/conversation") {
        val conversation = call.receive<Conversation>()
    }
}
