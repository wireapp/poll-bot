package com.wire.bots.polls.services

import com.wire.bots.polls.dto.messages.PollCreationMessage
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import mu.KLogging

data class ProxyConfiguration(val baseUrl: String)

class ProxySenderService(private val client: HttpClient, config: ProxyConfiguration) {

    private companion object : KLogging() {
        const val conversationPath = "/conversation"
    }

    private val conversationEndpoint = config.baseUrl + conversationPath

    suspend fun sendPoll(token: String, message: PollCreationMessage) {
        val response = client.post<String>(body = message) {
            url(conversationEndpoint)
            header("Authorization", "Bearer: $token")
        }
        logger.info { "Poll sent, response: $response" }
    }
}
