package com.wire.bots.polls.integration_tests.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.wire.bots.polls.integration_tests.dto.serialization.ConversationDeserializer

/**
 * Conversation API - receiving JSON from the bot.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = ConversationDeserializer::class)
data class Conversation(
    val type: String,
    val text: String? = null,
    val image: String? = null,
    val poll: Poll? = null
)

/**
 * Poll.
 */
interface Poll {
    /**
     * ID of the poll
     */
    val id: String
}

/**
 * Poll representation for the proxy.
 */
data class PollCreation(
    override val id: String,
    /**
     * Question asked.
     */
    val body: String,
    /**
     * Ordered options.
     */
    val buttons: List<String>
) : Poll {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PollCreation

        if (body != other.body) return false
        if (buttons != other.buttons) return false

        return true
    }

    override fun hashCode(): Int {
        var result = body.hashCode()
        result = 31 * result + buttons.hashCode()
        return result
    }
}

/**
 * Returns representation of this poll in the message.
 */
fun PollCreation.toCreateString() = "/poll \"$body\" ${buttons.joinToString(" ") { "\"$it\"" }}"

data class PollConfirmation(
    override val id: String,
    /**
     * Option voted.
     */
    val offset: Int,
    /**
     * User who voted.
     */
    val userId: String
) : Poll

fun pollConfirmationMessage(poll: PollConfirmation) = Conversation(
    type = "poll.action.confirmation",
    poll = poll
)

fun pollCreationMessage(poll: PollCreation) = Conversation(
    type = "poll.new",
    poll = poll
)

fun textMessage(text: String) = Conversation(
    type = "text",
    text = text
)

//{
//    "type": "string",
//    "text": "string",
//    "image": "string",
//    "poll": {
//    "id": "string",
//    "body": "string",
//    "buttons": [
//    "string"
//    ],
//    "offset": 0
//}
//}
