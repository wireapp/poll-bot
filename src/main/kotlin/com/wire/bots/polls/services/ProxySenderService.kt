package com.wire.bots.polls.services

import com.wire.bots.polls.dto.messages.BotMessage
import com.wire.bots.polls.dto.messages.ProxyResponseMessage
import com.wire.bots.polls.utils.createJson
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import mu.KLogging

class ProxySenderService(private val client: HttpClient, config: ProxyConfiguration) {

    private companion object : KLogging() {
        const val conversationPath = "/conversation"
    }

    private val conversationEndpoint = config.baseUrl + conversationPath

    suspend fun send(token: String, message: BotMessage): ProxyResponseMessage {
        logger.info { "Sending\n:${createJson(message)}" }
        val response = client.post<ProxyResponseMessage>(body = message) {
            url(conversationEndpoint)
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }

        logger.info { "Message sent, response: $response" }

        return response
    }
}

data class ProxyConfiguration(val baseUrl: String)
