package com.wire.bots.polls.integration_tests

import mu.KLogger
import mu.KLogging
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import kotlin.test.Test
import kotlin.test.assertEquals


class PollBotKtTest {

    private companion object : KLogging() {
        val application by lazy { startServer() }

        val kodein by lazy { application.kodein() }
    }

    @Test
    fun `test true true`() {
        assertEquals(expected = true, actual = true)
        val routingLogger by kodein.instance<KLogger>("routing-logger")
        routingLogger.info { "this is from test but seems like from routing" }
    }
}
