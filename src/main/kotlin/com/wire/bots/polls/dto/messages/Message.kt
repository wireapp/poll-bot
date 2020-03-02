package com.wire.bots.polls.dto.messages

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Message received by the bot from the proxy.
 *
 * TODO generate that from the swagger automatically
 */
@JsonIgnoreProperties(ignoreUnknown = true) // to continue working even though the JSON changed
data class Message(
    /**
     * ID of the bot = the bot should accept the message only when the ID matches.
     */
    val botId: String,
    /**
     * User who sent a message.
     */
    val userId: String?,
    /**
     * Type of the message.
     */
    // TODO this is basically enum, waiting for resolving top todo
    val type: String,
    /**
     * Message ID.
     */
    val messageId: String?,
    /**
     * Token that should be used for the reply.
     */
    val token: String?,
    /**
     * Text of the message.
     */
    val text: String?,
    /**
     * Id of the quoted message, when the user replies on something, this is id of something.
     */
    val refMessageId: String?,
    /**
     * When this and [refMessageId] is filled, the user liked the message with id [refMessageId].
     */
    // TODO replace with final name
    val reaction: String?,
    /**
     * Image in the message.
     */
    val image: String?,
    /**
     * Username who initiated conversation.
     */
    val handle: String?,
    /**
     * Language of the user.
     */
    val locale: String?,
    /**
     * Poll object.
     */
    val poll: PollObjectMessage?
)

/**
 * Poll representation for the proxy.
 */
data class PollObjectMessage(
    /**
     * Id of the poll.
     */
    val id: String,
    /**
     * Body of the poll - the question.
     */
    val body: String?,

    /**
     * Ordered list of buttons. Position in the list is the ID / offset.
     */
    val buttons: List<String>?,

    /**
     * Id of the button when it was clicked on.
     */
    val offset: Int?
)

/* JSON from the swagger
{
  "botId": "string",
  "userId": "string",
  "messageId": "string",
  "type": "string",
  "token": "string",
  "text": "string",
  "image": "string",
  "handle": "string",
  "locale": "string",
  "poll": {
    "id": "string",
    "body": "string",
    "buttons": [
      "string"
    ],
    "offset": "string",
    "userId": "string"
  }
}
 */
