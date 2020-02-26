package com.wire.bots.polls.dto.messages

data class PollActionConfirmationMessage(
    val poll: Poll,
    override val type: String = "poll.action.confirmation"
) : BotMessage {

    data class Poll(
        val id: String,
        val offset: Int,
        val userId: String
    )
}

/*
{
  "type" : "poll.action.confirmation",
  "poll" : {
    "id" : "24166f23-3477-4f2f-a7ca-44863d456fc8",
    "offset" : "1",
    "userId" : "2e06e56f-7e99-41e9-b3ba-185669bd52c1"
  }
}
 */
