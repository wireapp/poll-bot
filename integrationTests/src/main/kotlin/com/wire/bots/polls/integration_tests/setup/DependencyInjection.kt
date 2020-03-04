package com.wire.bots.polls.integration_tests.setup

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import io.ktor.util.KtorExperimentalAPI
import mu.KLogger
import mu.KLogging
import org.kodein.di.Kodein.MainBuilder
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

@KtorExperimentalAPI
fun MainBuilder.configureContainer() {

    bind<HttpClient>() with singleton {
        HttpClient(CIO) {
            install(WebSockets)
            install(JsonFeature) {
                serializer = JacksonSerializer()
            }
        }
    }

    bind<KLogger>("routing-logger") with singleton { KLogging().logger("Routing") }
    bind<KLogger>("install-logger") with singleton { KLogging().logger("KtorStartup") }
}
