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
    val handler by kodein().instance<MessagesHandlingService>()

    post("/messages") {
        val message = call.receive<Message>()
        runCatching {
            handler.handle(message)
            call.respond(HttpStatusCode.OK)
        }.onFailure {
            call.respond(HttpStatusCode.BadRequest, "Bot did not understand the message.")
        }
    }
}
