package com.wire.bots.polls.setup

import io.ktor.client.HttpClient
import io.ktor.client.features.logging.Logger
import org.slf4j.LoggerFactory

/**
 * Debug logger for HTTP requests.
 */
val Logger.Companion.DEBUG: Logger
    get() = object : Logger {
        private val delegate = LoggerFactory.getLogger(HttpClient::class.java)!!
        override fun log(message: String) {
            delegate.debug(message)
        }
    }

