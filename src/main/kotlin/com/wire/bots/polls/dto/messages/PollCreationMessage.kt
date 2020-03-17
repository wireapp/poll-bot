package com.wire.bots.polls.dto.messages


interface PollObject {
    val id: String
    val type: String
}

data class PollConfirmMessage(
    val poll: Poll,
    override val type: String = "poll"
) : BotMessage {

    data class Poll(
        override val id: String,
        /**
         * Option voted.
         */
        val offset: Int,
        /**
         * User who voted.
         */
        val userId: String,
        override val type: String = "confirmation"
    ) : PollObject
}

fun confirmVote(pollId: String, userId: String, offset: Int): BotMessage = PollConfirmMessage(
    poll = PollConfirmMessage.Poll(
        id = pollId,
        userId = userId,
        offset = offset
    )
)

data class NewPollMessage(
    val text: Text,
    val poll: Poll,
    override val type: String = "poll"
) : BotMessage {

    data class Text(
        val data: String
    )

    data class Poll(
        override val id: String,

        /**
         * Ordered options.
         */
        val buttons: List<String>,

        override val type: String = "create"
    ) : PollObject
}

fun newPollMessage(id: String, body: String, buttons: List<String>): BotMessage = NewPollMessage(
    text = NewPollMessage.Text(body),
    poll = NewPollMessage.Poll(
        id = id,
        buttons = buttons
    )
)

