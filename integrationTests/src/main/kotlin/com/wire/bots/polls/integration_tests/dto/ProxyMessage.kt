package com.wire.bots.polls.integration_tests.dto

/**
 * Message received by the bot from the proxy.
 *
 */
data class ProxyMessage(
    /**
     * ID of the bot = the bot should accept the message only when the ID matches.
     */
    val botId: String,
    /**
     * Type of the message.
     */
    val type: String,
    /**
     * User who sent a message.
     */
    val userId: String? = null,
    /**
     * Message ID.
     */
    val messageId: String? = null,
    /**
     * Token that should be used for the reply.
     */
    val token: String? = null,
    /**
     * Text of the message.
     */
    val text: String? = null,
    /**
     * Id of the quoted message, when the user replies on something, this is id of something.
     */
    val refMessageId: String? = null,
    /**
     * When this and [refMessageId] is filled, the user liked the message with id [refMessageId].
     */
    val reaction: String? = null
)

/**
 * To send bot request request.
 */
fun botRequest(userId: String, botId: String, token: String) = ProxyMessage(
    botId = botId,
    userId = userId,
    type = "conversation.bot_request",
    token = token
)

/**
 * To send init request.
 */
fun init(userId: String, botId: String, token: String) = ProxyMessage(
    botId = botId,
    userId = userId,
    type = "conversation.init",
    token = token
)

/**
 * To send new text.
 */
fun newText(userId: String, botId: String, token: String, text: String) = ProxyMessage(
    botId = botId,
    userId = userId,
    type = "conversation.new_text",
    token = token,
    text = text
)

/**
 * To send reaction.
 */
fun reaction(userId: String, botId: String, token: String, refMessageId: String) = ProxyMessage(
    botId = botId,
    userId = userId,
    type = "conversation.reaction",
    refMessageId = refMessageId,
    text = "some-emoji",
    token = token
)
