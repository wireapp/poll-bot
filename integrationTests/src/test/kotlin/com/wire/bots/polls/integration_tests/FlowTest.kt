package com.wire.bots.polls.integration_tests

import ai.blindspot.ktoolz.extensions.newLine
import com.wire.bots.polls.integration_tests.dto.botRequest
import com.wire.bots.polls.integration_tests.dto.init
import com.wire.bots.polls.integration_tests.dto.reaction
import com.wire.bots.polls.integration_tests.dto.textMessage
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

    @Test
    fun `test create poll and vote twice`() {
        val createdPoll = createPoll()

        val votingUser = randomStringUUID()

        vote(createdPoll.id, votingUser, 0)
        vote(createdPoll.id, votingUser, 1)
    }

    @Test
    fun `test vote and get results`() {
        val createdPoll = createPoll()
        val option = 1
        val votingUser = randomStringUUID()
        vote(createdPoll.id, votingUser, option)

        val token = randomStringUUID()

        val reactionMessage = reaction(
            userId = votingUser,
            botId = randomStringUUID(),
            token = token,
            refMessageId = createdPoll.id
        )

        val usersVoting = "0 - 0 votes${newLine}1 - 1 vote${newLine}2 - 0 votes"
        val expected = textMessage(
            "Results for pollId: `${createdPoll.id}`$newLine```$newLine$usersVoting$newLine```"
        )

        runBlocking {
            Application.botService.send(reactionMessage)
            delay(TIMEOUT)
        }

        assertEquals(expected, tokenStorage[token])
    }

    @Test
    fun `test change vote and get results`() {
        val createdPoll = createPoll()
        val votingUser = randomStringUUID()

        vote(createdPoll.id, votingUser, 1)

        val token = randomStringUUID()
        val reactionMessage = reaction(
            userId = votingUser,
            botId = randomStringUUID(),
            token = token,
            refMessageId = createdPoll.id
        )

        val usersVoting = "0 - 0 votes${newLine}1 - 1 vote${newLine}2 - 0 votes"
        val expected = textMessage(
            "Results for pollId: `${createdPoll.id}`$newLine```$newLine$usersVoting$newLine```"
        )

        runBlocking {
            Application.botService.send(reactionMessage)
            delay(TIMEOUT)
        }

        assertEquals(expected, tokenStorage[token])

        // now change the vote
        vote(createdPoll.id, votingUser, 0)

        val tokenWithChanged = randomStringUUID()
        val reactionMessageWithChangedVote = reaction(
            userId = votingUser,
            botId = randomStringUUID(),
            token = tokenWithChanged,
            refMessageId = createdPoll.id
        )

        val usersVotingWithChangedVote = "0 - 1 vote${newLine}1 - 0 votes${newLine}2 - 0 votes"
        val expectedWithChangedVote = textMessage(
            "Results for pollId: `${createdPoll.id}`$newLine```$newLine$usersVotingWithChangedVote$newLine```"
        )

        runBlocking {
            Application.botService.send(reactionMessageWithChangedVote)
            delay(TIMEOUT)
        }

        assertEquals(expectedWithChangedVote, tokenStorage[tokenWithChanged])

    }
}
