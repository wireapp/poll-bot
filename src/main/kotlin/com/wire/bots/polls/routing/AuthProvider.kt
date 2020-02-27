package com.wire.bots.polls.routing

import ai.blindspot.ktoolz.extensions.whenNull
import io.ktor.http.Headers
import mu.KLogging

class AuthProvider(private val proxyToken: String) {

    private companion object : KLogging() {
        const val authHeader = "Authorization"
        const val bearerPrefix = "Bearer "
    }

    fun isTokenValid(headersGet: () -> Headers) = isTokenValid(headersGet())

    private fun isTokenValid(headers: Headers): Boolean {
        val header = headers[authHeader].whenNull {
            logger.info { "Request did not have authorization header." }
        } ?: return false

        return if (!header.startsWith(bearerPrefix)) {
            false
        } else header.substringAfter(bearerPrefix) == proxyToken
    }
}
