package com.wire.bots.polls.setup

import com.fasterxml.jackson.databind.SerializationFeature
import com.wire.bots.polls.dao.DatabaseSetup
import com.wire.bots.polls.dto.conf.DatabaseConfiguration
import com.wire.bots.polls.routing.registerRoutes
import com.wire.bots.polls.setup.errors.registerExceptionHandlers
import com.wire.bots.polls.utils.createLogger
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.metrics.micrometer.MicrometerMetrics
import io.ktor.request.path
import io.ktor.routing.routing
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.flywaydb.core.Flyway
import org.kodein.di.LazyKodein
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import org.slf4j.event.Level
import java.text.DateFormat


private val installationLogger = createLogger("ApplicationSetup")

/**
 * Loads the application.
 */
fun Application.init() {
    setupKodein()
    // now kodein is running and can be used
    val k by kodein()
    installationLogger.debug { "DI container started." }

    // connect to the database
    connectDatabase(k)

    // configure Ktor
    installFrameworks(k)

    // register routing
    routing {
        registerRoutes()
    }
}

/**
 * Connect bot to the database.
 */
fun connectDatabase(k: LazyKodein) {
    installationLogger.info { "Connecting to the DB" }
    val dbConfig by k.instance<DatabaseConfiguration>()
    DatabaseSetup.connect(dbConfig)

    if (DatabaseSetup.isConnected()) {
        installationLogger.info { "DB connected." }
        migrateDatabase(dbConfig)
    } else {
        // TODO verify handling, maybe exit the App?
        installationLogger.error { "It was not possible to connect to db database! The application will start but it won't work." }
    }
}

/**
 * Migrate database using flyway.
 */
fun migrateDatabase(dbConfig: DatabaseConfiguration) {
    installationLogger.info { "Migrating database." }
    val migrationsCount = Flyway
        .configure()
        .dataSource(dbConfig.url, dbConfig.userName, dbConfig.password)
        .load()
        .migrate()

    installationLogger.info { if (migrationsCount == 0) "No migrations necessary." else "Applied $migrationsCount migrations." }
}

/**
 * Configure Ktor and install necessary extensions.
 */
fun Application.installFrameworks(k: LazyKodein) {
    install(ContentNegotiation) {
        jackson {
            // enable pretty print for JSONs
            enable(SerializationFeature.INDENT_OUTPUT)
            dateFormat = DateFormat.getDateTimeInstance()
        }
    }

    install(DefaultHeaders)
    install(CallLogging) {
        level = Level.TRACE
        logger = createLogger("HttpCallLogger")

        filter { call -> call.request.path().startsWith("/messages") }
    }

    configurePrometheus(k)
    registerExceptionHandlers(k)
}

fun Application.configurePrometheus(k: LazyKodein) {
    val prometheusRegistry by k.instance<PrometheusMeterRegistry>()
    install(MicrometerMetrics) {
        registry = prometheusRegistry
        distributionStatisticConfig = DistributionStatisticConfig.Builder()
            .percentilesHistogram(true)
            .build()
    }
}
