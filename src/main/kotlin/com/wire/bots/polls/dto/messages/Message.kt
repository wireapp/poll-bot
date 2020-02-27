package com.wire.bots.polls.dto.messages

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Message received by the bot from the proxy.
 *
 * TODO generate that from the swagger automatically
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Message(
    val botId: String,
    val userId: String?,
    // this is basically enum, waiting for resolving top TODO
    val type: String,
    val messageId: String?,
    val token: String?,
    val text: String?,
    val image: String?,
    val handle: String?,
    val locale: String?,
    val poll: PollObjectMessage?
)

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
    val offset: Int?,

    /**
     * Id of the user who clicked button.
     */
    val userId: String?
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
