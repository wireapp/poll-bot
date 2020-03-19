package com.wire.bots.polls.dto.bot

import com.wire.bots.polls.dto.common.Mention
import com.wire.bots.polls.dto.common.Text

/**
 * Creates message which greets the users in the conversation.
 */
fun greeting(text: String, mentions: List<Mention> = emptyList()): BotMessage = Greeting(
    text = Text(
        data = text,
        mentions = mentions
    )
)

/**
 * Creates message for poll.
 */
fun newPoll(id: String, body: String, buttons: List<String>, mentions: List<Mention> = emptyList()): BotMessage = NewPoll(
    text = Text(body, mentions),
    poll = NewPoll.Poll(
        id = id,
        buttons = buttons
    )
)

/**
 * Creates message for vote confirmation.
 */
fun confirmVote(pollId: String, userId: String, offset: Int): BotMessage = PollVote(
    poll = PollVote.Poll(
        id = pollId,
        userId = userId,
        offset = offset
    )
)

/**
 * Creates stats (result of the poll) message.
 */
fun statsMessage(text: String, mentions: List<Mention> = emptyList()): BotMessage = Stats(
    text = Text(
        data = text,
        mentions = mentions
    )
)

/**
 * Creates message notifying user about wrongly used command.
 */
fun fallBackMessage(text: String, mentions: List<Mention> = emptyList()): BotMessage = FallbackMessage(
    text = Text(
        data = text,
        mentions = mentions
    )
)
