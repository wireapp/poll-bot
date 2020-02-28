package com.wire.bots.polls.routing

import com.wire.bots.polls.dao.DatabaseSetup
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
        if (DatabaseSetup.isConnected()) {
            call.respond("healthy")
        } else {
            call.respond(HttpStatusCode.ServiceUnavailable, "DB connection is not working")
        }
    }
}
