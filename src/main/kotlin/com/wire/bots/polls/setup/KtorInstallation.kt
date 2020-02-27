package com.wire.bots.polls.setup

import com.fasterxml.jackson.databind.SerializationFeature
import com.wire.bots.polls.routing.registerRoutes
import com.wire.bots.polls.websockets.subscribeToWebSockets
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.ktor.websocket.WebSockets
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import java.text.DateFormat
import java.time.Duration

@KtorExperimentalAPI
fun Application.init() {
    setupKodein()

    val k by kodein()

    installFrameworks()

    routing {
        registerRoutes()
    }

    val useWebSockets by k.instance<Boolean>("use-websocket")

    if (useWebSockets) {
        subscribeToWebSockets()
    }
}

fun Application.installFrameworks() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            dateFormat = DateFormat.getDateTimeInstance()
        }
    }

    // Install Ktor features
    install(DefaultHeaders)
    install(CallLogging)

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(60) // Disabled (null) by default
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE // Disabled (max value). The connection will be closed if surpassed this length.
        masking = false
    }

}
