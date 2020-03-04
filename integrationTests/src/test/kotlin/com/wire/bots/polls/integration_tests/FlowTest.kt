package com.wire.bots.polls.integration_tests

import com.wire.bots.polls.integration_tests.dto.PollConfirmation
import com.wire.bots.polls.integration_tests.dto.PollCreation
import com.wire.bots.polls.integration_tests.dto.ProxyMessage
import com.wire.bots.polls.integration_tests.dto.botRequest
import com.wire.bots.polls.integration_tests.dto.init
import com.wire.bots.polls.integration_tests.dto.newText
import com.wire.bots.polls.integration_tests.dto.pollConfirmationMessage
import com.wire.bots.polls.integration_tests.dto.textMessage
import com.wire.bots.polls.integration_tests.dto.toCreateString
import com.wire.bots.polls.integration_tests.dto.voteUsingObject
import com.wire.bots.polls.integration_tests.dto.voteUsingText
import com.wire.bots.polls.integration_tests.store.tokenStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test
import kotlin.test.assertEquals


class FlowTest {

    companion object : KLogging() {
        private const val TIMEOUT = 500L

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            // this call initializes the engine and thus starts the application
            Application.engine
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            // stops the running application
            Application.teardown()
            print("tearing down")
        }
    }

    @Test
    fun `test new bot request`() {
        val botMessage = botRequest(
            userId = randomStringUUID(),
            botId = randomStringUUID(),
            token = randomStringUUID()
        )

        // bot won't send new message to that so we are just checking whether bot responds with OK
        runBlocking {
            Application.botService.send(botMessage)
        }
    }

    @Test
    fun `test init conversation`() {
        val token = randomStringUUID()
        val initMessage = init(
            userId = randomStringUUID(),
            botId = randomStringUUID(),
            token = token
        )

        val expectedMessage = textMessage("To create poll please text: /poll \"Question\" \"Option 1\" \"Option 2\"")

        runBlocking {
            Application.botService.send(initMessage)
        }

        assertEquals(expectedMessage, tokenStorage[token])
    }

    @Test
    fun `test create poll`() {
        createPoll()
    }

    @Test
    fun `test create poll and vote via text`() {
        val createdPoll = createPoll()

        val token = randomStringUUID()
        val option = 0
        val votingUser = randomStringUUID()


        vote(token, createdPoll.id, votingUser, option) {
            voteUsingText(
                userId = votingUser,
                botId = randomStringUUID(),
                token = token,
                pollId = createdPoll.id,
                option = option
            )
        }
    }

    @Test
    fun `test create poll and vote via object`() {
        val createdPoll = createPoll()

        val token = randomStringUUID()
        val option = 1
        val votingUser = randomStringUUID()


        vote(token, createdPoll.id, votingUser, option) {
            voteUsingObject(
                userId = votingUser,
                botId = randomStringUUID(),
                token = token,
                pollId = createdPoll.id,
                option = option
            )
        }
    }

    private fun vote(token: String, pollId: String, votingUserId: String, option: Int, voteOption: () -> ProxyMessage) {
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

    private fun createPoll(): PollCreation {
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

}
