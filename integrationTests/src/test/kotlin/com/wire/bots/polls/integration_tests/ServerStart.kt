package com.wire.bots.polls.integration_tests

import com.wire.bots.polls.integration_tests.setup.init
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.util.UUID

/**
 * Starts the Ktor api
 */
@Suppress("EXPERIMENTAL_API_USAGE") // because we don't want to propagate that further
fun startServer() = embeddedServer(Netty, 8081, module = Application::init).start()


/**
 * Generates UUID and returns it's string representation.
 */
fun randomStringUUID() = UUID.randomUUID().toString()
