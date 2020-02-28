package com.wire.bots.polls.routing

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get

/**
 * Health indication endpoints.
 */
fun Routing.healthStatus() {
    /**
     * Responds only 200 for ingres.
     */
    get("/status") {
        call.respond(HttpStatusCode.OK)
    }

    /**
     * More complex API for indication of all resources.
     */
    get("/status/health") {
        // TODO implement some kind of health check for DB
        call.respond("healthy")
    }
}
