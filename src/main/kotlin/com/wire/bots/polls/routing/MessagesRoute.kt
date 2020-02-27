package com.wire.bots.polls.routing

import com.wire.bots.polls.dto.messages.Message
import com.wire.bots.polls.services.MessagesHandlingService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein

fun Routing.messages() {
    val k by kodein()
    val handler by k.instance<MessagesHandlingService>()
    val authProvider by k.instance<AuthProvider>()

    post("/messages") {
        if (authProvider.isTokenValid { call.request.headers }) {
            val message = call.receive<Message>()
            runCatching {
                handler.handle(message)
                call.respond(HttpStatusCode.OK)
            }.onFailure {
                call.respond(HttpStatusCode.BadRequest, "Bot did not understand the message.")
            }
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Please provide Authorization header.")
        }
    }
}
