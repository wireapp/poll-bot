package com.wire.bots.polls.integration_tests.routing

import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.bots.polls.integration_tests.dto.Conversation
import com.wire.bots.polls.integration_tests.store.tokenStorage
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
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
        val token = call.request.header("Authorization")?.substringAfter("Bearer ")?.whenNull {
            call.respond(HttpStatusCode.Unauthorized, "Missing header Authorization")
        } ?: return@post
        tokenStorage[token] = conversation
    }
}
