package com.wire.bots.polls.integration_tests

import com.wire.bots.polls.integration_tests.dto.PollConfirmation
import com.wire.bots.polls.integration_tests.dto.PollCreation
import com.wire.bots.polls.integration_tests.dto.ProxyMessage
import com.wire.bots.polls.integration_tests.dto.newText
import com.wire.bots.polls.integration_tests.dto.pollConfirmationMessage
import com.wire.bots.polls.integration_tests.dto.toCreateString
import com.wire.bots.polls.integration_tests.dto.voteUsingObject
import com.wire.bots.polls.integration_tests.store.tokenStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals

const val TIMEOUT = 500L

fun vote(pollId: String, votingUserId: String, option: Int) {
    val token = randomStringUUID()

    vote(token, pollId, votingUserId, option) {
        voteUsingObject(
            userId = votingUserId,
            botId = randomStringUUID(),
            token = token,
            pollId = pollId,
            option = option
        )
    }
}

fun vote(token: String, pollId: String, votingUserId: String, option: Int, voteOption: () -> ProxyMessage) {
    val vote = voteOption()

    val expected = pollConfirmationMessage(
        PollConfirmation(
            id = pollId,
            offset = option,
            userId = votingUserId
        )
    )

    runBlocking {
        Application.botService.send(vote)
        delay(TIMEOUT)
    }

    assertEquals(expected, tokenStorage[token])
}

fun createPoll(): PollCreation {
    val token = randomStringUUID()

    val pollCreation = PollCreation(
        id = randomStringUUID(), // we don't care about this
        body = "Who is the best?",
        buttons = listOf("Dejan", "Lukas", "Whole Wire")
    )

    val pollTextMessage = newText(
        userId = randomStringUUID(),
        botId = randomStringUUID(),
        token = token,
        text = pollCreation.toCreateString()
    )

    runBlocking {
        Application.botService.send(pollTextMessage)
        delay(TIMEOUT)
    }

    val receivedMessage = requireNotNull(tokenStorage[token]) {
        "There was no data under the token! That means bot did not send anything"
    }

    assertEquals("poll.new", receivedMessage.type)
    // the equal in PollCreation is overridden so it does not use Id to compare objects
    assertEquals(pollCreation, receivedMessage.poll)

    return requireNotNull(receivedMessage.poll as? PollCreation) { "Poll in received message was not poll creation. This is weird." }
}
