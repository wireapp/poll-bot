package com.wire.bots.polls.dto.roman

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.wire.bots.polls.dto.common.Mention

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
     * Id of the conversation.
     */
    val conversationId: String?,

    /**
     * Type of the message.
     */
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
    val poll: PollObjectMessage?,

    /**
     * Type of the file
     */
    val mimeType: String?,

    /**
     * Mentions in the code
     */
    val mentions: List<Mention>?
) {
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
}

/* JSON from the swagger
{
  "botId": "string",
  "type": "string",
  "userId": "string",
  "messageId": "string",
  "conversationId": "string",
  "token": "string",
  "text": "string",
  "image": "string",
  "attachment": "string",
  "handle": "string",
  "locale": "string",
  "poll": {
    "id": "string",
    "type": "string",
    "buttons": [
      "string"
    ],
    "offset": 0,
    "userId": "string"
  },
  "refMessageId": "string",
  "mimeType": "string",
  "mentions": [
    {
      "userId": "string",
      "offset": 0,
      "length": 0
    }
  ]
}
 */
