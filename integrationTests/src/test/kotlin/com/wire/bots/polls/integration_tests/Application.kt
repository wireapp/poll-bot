package com.wire.bots.polls.integration_tests

import com.wire.bots.polls.integration_tests.services.BotApiService
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein

/**
 * Object storing access for the running application.
 */
object Application {

    /**
     * Running engine.
     */
    val engine by lazy { startServer() }

    /**
     * Current running Application.
     */
    val application by lazy { engine.application }

    /**
     * Kodein from the running application.
     */
    val appKodein by lazy { application.kodein() }

    /**
     * Connection to the bot.
     */
    val botService by lazy { val api by appKodein.instance<BotApiService>(); api }

    /**
     * Stops the application.
     */
    fun teardown() = engine.stop(500L, 1000L)
}
