package com.wire.bots.polls.integration_tests.setup

import io.ktor.application.Application
import io.ktor.util.KtorExperimentalAPI
import org.kodein.di.ktor.kodein

/**
 * Inits and sets up DI container.
 */
@KtorExperimentalAPI
fun Application.setupKodein() {
    kodein {
        bindConfiguration()
        configureContainer()
    }
}
