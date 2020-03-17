package com.wire.bots.polls.dto.bot

import com.wire.bots.polls.dto.common.Mention
import com.wire.bots.polls.dto.common.Text

fun greeting(text: String, mentions: List<Mention> = emptyList()): BotMessage = Greeting(
    text = Text(
        data = text,
        mentions = mentions
    )
)

fun newPoll(id: String, body: String, buttons: List<String>, mentions: List<Mention> = emptyList()): BotMessage = NewPoll(
    text = Text(body, mentions),
    poll = NewPoll.Poll(
        id = id,
        buttons = buttons
    )
)

fun confirmVote(pollId: String, userId: String, offset: Int): BotMessage = PollVote(
    poll = PollVote.Poll(
        id = pollId,
        userId = userId,
        offset = offset
    )
)

fun statsMessage(text: String, mentions: List<Mention> = emptyList()): BotMessage = Stats(
    text = Text(
        data = text,
        mentions = mentions
    )
)
