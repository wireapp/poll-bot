package com.wire.bots.polls.routing

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.healthStatus() {
    get("/status") {
        call.respond(HttpStatusCode.OK)
    }

    get("/status/health") {
        // TODO implement some kind of health check for DB
        call.respond("healthy")
    }
}
