package com.wire.bots.polls.dto.messages

/**
 * Users input for the bot.
 *
 * TODO generate that from the swagger automatically
 */
data class UsersMessage(
    val botId: String,
    val userId: String,
    // this is basically enum, waiting for resolving top TODO
    val type: String,
    val token: String?,
    val text: String?,
    val image: String?,
    val handle: String?,
    val locale: String?
)

/* JSON from the swagger
{
  "botId": "string",
  "userId": "string",
  "type": "string",
  "token": "string",
  "text": "string",
  "image": "string",
  "handle": "string",
  "locale": "string"
}
 */
