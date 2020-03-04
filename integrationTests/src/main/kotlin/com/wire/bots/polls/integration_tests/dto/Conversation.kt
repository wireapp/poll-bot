package com.wire.bots.polls.integration_tests.dto

/**
 * Conversation API - receiving JSON from the bot.
 */
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
) : Poll

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
