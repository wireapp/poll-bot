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
import mu.KLogger
import org.jetbrains.exposed.sql.Database
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import java.text.DateFormat
import java.time.Duration

/**
 * Loads the application.
 */
@KtorExperimentalAPI
fun Application.init() {
    setupKodein()
    // now kodein is running and can be used
    val k by kodein()
    val logger by k.instance<KLogger>("install-logger")
    logger.debug { "DI container started." }

    // connect to the database
    logger.debug { "Connecting to the DB" }
    val connectionString by k.instance<String>("db-connection-string")
    Database.connect(connectionString, driver = "org.postgresql.Driver")
    logger.debug { "DB connected." }

    // configure Ktor
    installFrameworks()

    // register routing
    routing {
        registerRoutes()
    }

    // determine whether should bot connect to the proxy web socket
    val useWebSockets by k.instance<Boolean>("use-websocket")
    if (useWebSockets) {
        subscribeToWebSockets()
    }
}

/**
 * Configure Ktor and install necessary extensions.
 */
fun Application.installFrameworks() {
    install(ContentNegotiation) {
        jackson {
            // enable pretty print for JSONs
            enable(SerializationFeature.INDENT_OUTPUT)
            dateFormat = DateFormat.getDateTimeInstance()
        }
    }

    install(DefaultHeaders)
    install(CallLogging)

    install(WebSockets) {
        // enable ping - to keep the connection alive
        pingPeriod = Duration.ofSeconds(30)
        timeout = Duration.ofSeconds(15)
        // disabled (max value) - the connection will be closed if surpassed this length.
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

}
