package com.wire.bots.polls.setup

import com.wire.bots.polls.filters.TypeFilter
import com.wire.bots.polls.parser.InputParser
import com.wire.bots.polls.parser.InputValidation
import com.wire.bots.polls.parser.PollFactory
import com.wire.bots.polls.parser.PollValidation
import com.wire.bots.polls.services.PollService
import com.wire.bots.polls.services.ProxyConfiguration
import com.wire.bots.polls.services.ProxySenderService
import com.wire.bots.polls.websockets.PollWebSocket
import com.wire.bots.polls.websockets.WebSocketConfig
import io.ktor.application.Application
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import io.ktor.util.KtorExperimentalAPI
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import org.kodein.di.ktor.kodein

@KtorExperimentalAPI
fun Application.setupKodein() {
    kodein {
        bind<TypeFilter>() with singleton { TypeFilter() }

        bind<InputValidation>() with singleton { InputValidation() }
        bind<PollValidation>() with singleton { PollValidation() }

        bind<HttpClient>() with singleton {
            HttpClient {
                install(WebSockets)
                install(JsonFeature) {
                    serializer = JacksonSerializer()
                }
            }
        }

        bind<PollWebSocket>() with singleton {
            PollWebSocket(
                instance(),
                WebSocketConfig(host = "127.0.0.1", port = 1234, path = ""),
                instance()
            )
        }

        bind<ProxySenderService>() with singleton {
            ProxySenderService(
                instance(),
                config = ProxyConfiguration("https://proxy.services.wire.com")
            )
        }

        bind<InputParser>() with singleton { InputParser(instance()) }

        bind<PollFactory>() with singleton { PollFactory(instance(), instance()) }
        bind<PollService>() with provider { PollService(instance(), instance()) }
    }
}
