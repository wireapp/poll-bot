package com.wire.bots.polls.integration_tests.setup

import ai.blindspot.ktoolz.extensions.getEnv
import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.bots.polls.integration_tests.dto.BotApiConfiguration
import com.wire.bots.polls.integration_tests.setup.EnvConfigVariables.BOT_API
import com.wire.bots.polls.integration_tests.setup.EnvConfigVariables.SERVICE_TOKEN
import mu.KLogging
import org.kodein.di.Kodein.MainBuilder
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

private val logger = KLogging().logger("EnvironmentLoaderLogger")

private fun getEnvOrLogDefault(env: String, defaultValue: String) = getEnv(env).whenNull {
    logger.warn { "Env variable $env not set! Using default value - $defaultValue" }
} ?: defaultValue

/**
 * Loads the DI container with configuration from the system environment.
 */
fun MainBuilder.bindConfiguration() {

    bind<BotApiConfiguration>() with singleton {
        BotApiConfiguration(
            baseUrl = instance("bot-api-url"),
            token = instance("proxy-auth")
        )
    }

    bind<String>("bot-api-url") with singleton {
        getEnvOrLogDefault(BOT_API, "http://localhost:8080")
    }

    bind<String>("proxy-auth") with singleton {
        getEnvOrLogDefault(SERVICE_TOKEN, "local-token")
    }
}
