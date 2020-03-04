package com.wire.bots.polls.integration_tests.services

import com.wire.bots.polls.integration_tests.dto.BotApiConfiguration
import com.wire.bots.polls.integration_tests.dto.ProxyMessage
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import mu.KLogging

/**
 * Service for communication with bot.
 */
class BotApiService(private val client: HttpClient, private val botApi: BotApiConfiguration) {

    private companion object : KLogging() {
        const val messagesPath = "/messages"
    }

    /**
     * Sends conversation message to the bot.
     */
    suspend fun send(message: ProxyMessage) {
        val endpoint = botApi.baseUrl + messagesPath

        val response = client.post<HttpResponse>(body = message) {
            url(endpoint)
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${botApi.token}")
        }

        if (!response.status.isSuccess()) {
            throw Exception("Response is not success! Actual response: ${response.status}.")
        }
    }
}
