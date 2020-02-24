package com.wire.bots.polls.setup

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.jackson.jackson
import io.ktor.websocket.WebSockets
import java.text.DateFormat
import java.time.Duration

fun Application.installFrameworks() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            dateFormat = DateFormat.getDateTimeInstance()
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
}
