package com.wire.bots.polls.routing

import com.wire.bots.polls.dto.roman.Message
import com.wire.bots.polls.services.AuthService
import com.wire.bots.polls.services.MessagesHandlingService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import mu.KLogger
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein

/**
 * Messages API.
 */
fun Routing.messages() {
    val k by kodein()

    val logger by k.instance<KLogger>("routing-logger")
    val handler by k.instance<MessagesHandlingService>()
    val authService by k.instance<AuthService>()

    post("/messages") {
        logger.debug { "POST /messages" }
        // verify whether request contain correct auth header
        if (authService.isTokenValid { call.request.headers }) {
            logger.info { "Token is valid." }
            // bot responds either with 200 or with 400
            runCatching {
                logger.info { "Parsing an message." }
                val message = call.receive<Message>()
                logger.info { "Message parsed." }
                handler.handle(message)
                logger.info { "Responding OK" }
                call.respond(HttpStatusCode.OK)
            }.onFailure {
                logger.error(it) { "Exception occurred during the request handling!" }
                call.respond(HttpStatusCode.BadRequest, "Bot did not understand the message.")
            }
        } else {
            logger.info { "Token is invalid." }
            call.respond(HttpStatusCode.Unauthorized, "Please provide Authorization header.")
        }
    }
}
