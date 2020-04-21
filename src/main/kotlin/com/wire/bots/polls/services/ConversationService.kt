package com.wire.bots.polls.services

import ai.blindspot.ktoolz.extensions.parseJson
import com.wire.bots.polls.dto.roman.ConversationInformation
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.client.statement.HttpStatement
import mu.KLogging

/**
 * Provides possibility to check the conversation details.
 */
class ConversationService(private val client: HttpClient, config: ProxyConfiguration) {

    private companion object : KLogging() {
        const val conversationPath = "/conversation"
    }

    private val endpoint = config.baseUrl + conversationPath

    /**
     * Returns the number of members of conversation.
     */
    suspend fun getNumberOfConversationMembers(token: String): Int? =
        runCatching {
            val response = client.get<HttpStatement> {
                url(endpoint)
                header("Authorization", "Bearer $token")
            }.execute()
            logger.trace { "Executed" }

            val payload = response.receive<String>()
            logger.trace { payload }
            parseJson<ConversationInformation>(payload)
        }.onFailure {
            logger.error(it) { "It was not possible to fetch conversation information!" }
        }.onSuccess {
            logger.debug { "Successfully got conversation information." }
            logger.trace { it }
        }.getOrNull()
            ?.members
            ?.filter { it.service == null }
            ?.size
}
