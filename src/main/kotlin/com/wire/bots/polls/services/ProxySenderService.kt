package com.wire.bots.polls.services

import com.wire.bots.polls.dto.bot.BotMessage
import com.wire.bots.polls.dto.roman.Response
import com.wire.bots.polls.utils.createJson
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import mu.KLogging

/**
 * Service responsible for sending requests to the proxy service Roman.
 */
class ProxySenderService(private val client: HttpClient, config: ProxyConfiguration) {

    private companion object : KLogging() {
        const val conversationPath = "/conversation"
    }

    private val conversationEndpoint = config.baseUrl + conversationPath

    /**
     * Send given message with provided token.
     */
    suspend fun send(token: String, message: BotMessage): Response {
        logger.debug { "Sending\n:${createJson(message)}" }

        val response = client.post<Response>(body = message) {
            url(conversationEndpoint)
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }

        logger.debug { "Message sent, response: $response" }

        return response
    }
}

/**
 * Configuration used to connect to the proxy.
 */
data class ProxyConfiguration(val baseUrl: String)
