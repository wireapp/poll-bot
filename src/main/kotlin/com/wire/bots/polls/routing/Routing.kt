package com.wire.bots.polls.routing

import com.wire.bots.polls.dto.messages.UsersMessage
import com.wire.bots.polls.services.PollService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein

fun Routing.registerRoutes() {

    val kodein by kodein()
    val pollService by kodein.instance<PollService>()

    post("/messages") {
        val usersMessage = call.receive<UsersMessage>()
        val poll = pollService.createPoll(usersMessage)

        if (poll != null) {
            call.respond(poll)
        } else {
            call.respond(HttpStatusCode.BadRequest, "It was not possible to create poll.")
        }
    }

    get("/") {
        call.respond("Hello from the bot.")
    }
}
