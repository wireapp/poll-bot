package com.wire.bots.polls.setup

import com.wire.bots.polls.utils.countException
import com.wire.bots.polls.utils.createLogger
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.kodein.di.LazyKodein
import org.kodein.di.generic.instance

private val logger = createLogger("ExceptionHandler")

/**
 * Registers exception handling.
 */
fun Application.registerExceptionHandlers(k: LazyKodein) {
    val registry by k.instance<PrometheusMeterRegistry>()

    install(StatusPages) {
        exception<Exception> { cause ->
            logger.error(cause) { "Exception occurred in the application: ${cause.message}" }
            call.errorResponse(HttpStatusCode.InternalServerError, cause.message)
            registry.countException(cause)
        }
    }
}

suspend inline fun ApplicationCall.errorResponse(statusCode: HttpStatusCode, message: String?) {
    respond(status = statusCode, message = mapOf("message" to (message ?: "No details specified")))
}
