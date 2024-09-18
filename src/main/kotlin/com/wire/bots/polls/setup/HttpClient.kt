package com.wire.bots.polls.setup

import com.wire.bots.polls.utils.ClientRequestMetric
import com.wire.bots.polls.utils.createLogger
import com.wire.bots.polls.utils.httpCall
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.micrometer.core.instrument.MeterRegistry

fun createHttpClient(meterRegistry: MeterRegistry) =
    HttpClient(Apache) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }

        install(ClientRequestMetric) {
            onResponse { meterRegistry.httpCall(it) }
        }

        install(Logging) {
            logger = Logger.TRACE
            level = LogLevel.ALL
        }
    }

/**
 * Trace logger for HTTP Requests.
 *
 * Logs request/response bodies, params and headers.
 * Avoids logging lines containing sensitive data
 */
private val Logger.Companion.TRACE: Logger
    get() = object : Logger, org.slf4j.Logger by createLogger("TraceHttpClient") {
        override fun log(message: String) {
            for (blockedWord in blockedWordList) {
                if (message.contains(blockedWord, ignoreCase = true)) {
                    return
                }
            }
            trace(message)
        }
    }

private val blockedWordList = listOf("Authorization", "token", "Bearer", "text")
