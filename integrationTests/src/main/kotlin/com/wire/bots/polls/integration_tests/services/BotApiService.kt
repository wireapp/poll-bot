package com.wire.bots.polls.integration_tests.services

import com.wire.bots.polls.integration_tests.dto.BotApiConfiguration
import com.wire.bots.polls.integration_tests.dto.Conversation
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
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
    suspend fun send(conversation: Conversation) {
        val endpoint = botApi.baseUrl + messagesPath

        client.post<Any>(body = conversation) {
            url(endpoint)
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${botApi.token}")
        }
    }
}
