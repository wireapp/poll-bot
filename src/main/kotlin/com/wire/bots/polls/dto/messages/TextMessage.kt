package com.wire.bots.polls.dto.messages

/**
 * Message sent to Proxy which contains only text.
 */
data class TextMessage(
    /**
     * Bot's text.
     */
    val text: Text,
    override val type: String = "text"
) : BotMessage {

    constructor(text: String) : this(Text(text, emptyList()))

    data class Text(
        val data: String,
        val mentions: List<Mention>
    )
}
