package com.wire.bots.polls.integration_tests

import com.wire.bots.polls.integration_tests.setup.init
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8081, module = Application::init).start()
}
