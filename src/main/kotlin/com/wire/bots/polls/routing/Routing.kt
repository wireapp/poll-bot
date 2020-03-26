package com.wire.bots.polls.routing

import ai.blindspot.ktoolz.extensions.newLine
import io.ktor.application.call
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein

/**
 * Register routes to the KTor.
 */
fun Routing.registerRoutes() {

    val k by kodein()
    val version by k.instance<String>("version")

    get("/") {
        call.respond("This is the Wire Poll Bot.")
    }

    get("/version") {
        call.respond(TextContent("{\"version\": \"$version\"}$newLine", ContentType.Application.Json))
    }

    healthStatus()
    messages()
}
