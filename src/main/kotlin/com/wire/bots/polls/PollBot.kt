package com.wire.bots.polls

import com.wire.bots.polls.setup.init
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
fun main(args: Array<String>) {
    embeddedServer(Netty, 8080, module = Application::init).start()
}