package com.wire.bots.polls.integration_tests

import com.wire.bots.polls.integration_tests.dto.botRequest
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test


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
        val botReq = botRequest(
            userId = randomStringUUID(),
            botId = randomStringUUID(),
            token = randomStringUUID()
        )

        runBlocking {
            Application.botService.send(botReq)
        }
    }
}
