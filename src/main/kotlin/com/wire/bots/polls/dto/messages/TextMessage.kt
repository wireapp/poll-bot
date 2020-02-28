package com.wire.bots.polls.dto.messages

/**
 * Message sent to Proxy which contains only text.
 */
data class TextMessage(
    /**
     * Bot's text.
     */
    val text: String,
    override val type: String = "text"
) : BotMessage
