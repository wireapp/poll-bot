package com.wire.bots.polls.setup

import com.wire.bots.polls.filters.TypeFilter
import com.wire.bots.polls.parser.InputParser
import com.wire.bots.polls.parser.InputValidation
import com.wire.bots.polls.parser.PollFactory
import com.wire.bots.polls.parser.PollValidation
import com.wire.bots.polls.routing.AuthProvider
import com.wire.bots.polls.services.MessagesHandlingService
import com.wire.bots.polls.services.PollService
import com.wire.bots.polls.services.ProxySenderService
import com.wire.bots.polls.websockets.PollWebSocket
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import io.ktor.util.KtorExperimentalAPI
import org.kodein.di.Kodein.MainBuilder
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

@KtorExperimentalAPI
fun MainBuilder.configureContainer() {
    bind<TypeFilter>() with singleton { TypeFilter() }

    bind<InputValidation>() with singleton { InputValidation() }
    bind<PollValidation>() with singleton { PollValidation() }

    bind<HttpClient>() with singleton {
        HttpClient(CIO) {
            install(WebSockets)
            install(JsonFeature) {
                serializer = JacksonSerializer()
            }
        }
    }

    bind<PollWebSocket>() with singleton {
        PollWebSocket(
            instance(),
            instance(),
            instance()
        )
    }

    bind<ProxySenderService>() with singleton {
        ProxySenderService(
            instance(),
            instance()
        )
    }

    bind<InputParser>() with singleton { InputParser(instance()) }

    bind<PollFactory>() with singleton { PollFactory(instance(), instance()) }

    bind<PollService>() with singleton { PollService(instance(), instance()) }

    bind<MessagesHandlingService>() with singleton { MessagesHandlingService(instance(), instance()) }

    bind<AuthProvider>() with singleton { AuthProvider(instance("proxy-auth")) }
}
