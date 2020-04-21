package com.wire.bots.polls.setup

import io.ktor.application.Application
import org.kodein.di.ktor.kodein

/**
 * Inits and sets up DI container.
 */
fun Application.setupKodein() {
    kodein {
        bindConfiguration()
        configureContainer()
    }
}
