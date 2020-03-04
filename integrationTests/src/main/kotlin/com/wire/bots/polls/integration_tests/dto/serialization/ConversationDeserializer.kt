package com.wire.bots.polls.integration_tests.dto.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wire.bots.polls.integration_tests.dto.Conversation
import com.wire.bots.polls.integration_tests.dto.Poll
import com.wire.bots.polls.integration_tests.dto.PollConfirmation
import com.wire.bots.polls.integration_tests.dto.PollCreation

/**
 * Custom deserialization for the [Conversation] class.
 */
class ConversationDeserializer : JsonDeserializer<Conversation>() {

    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): Conversation {
        val root = jp.readValueAsTree<JsonNode>()

        val pollNode = root["poll"] ?: return root.toConversation()

        val mapper = jacksonObjectMapper()
        val poll = when (root["type"]?.asText()) {
            "poll.action.confirmation" -> mapper.readValue<PollConfirmation>(pollNode.toString())
            "poll.new" -> mapper.readValue<PollCreation>(pollNode.toString())
            else -> throw IllegalArgumentException("Invalid type for the poll!")
        }

        return root.toConversation(poll)
    }

    private fun JsonNode.toConversation(poll: Poll? = null) = Conversation(
        type = this["type"].asText(),
        text = this["text"]?.asText(),
        image = this["image"]?.asText(),
        poll = poll
    )
}
