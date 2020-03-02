package com.wire.bots.polls.setup

import ai.blindspot.ktoolz.extensions.getEnv
import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.bots.polls.dto.messages.DatabaseConfiguration
import com.wire.bots.polls.services.ProxyConfiguration
import com.wire.bots.polls.setup.EnvConfigVariables.APP_KEY
import com.wire.bots.polls.setup.EnvConfigVariables.DB_PASSWORD
import com.wire.bots.polls.setup.EnvConfigVariables.DB_URL
import com.wire.bots.polls.setup.EnvConfigVariables.DB_USER
import com.wire.bots.polls.setup.EnvConfigVariables.PROXY_DOMAIN
import com.wire.bots.polls.setup.EnvConfigVariables.PROXY_WS_HOST
import com.wire.bots.polls.setup.EnvConfigVariables.PROXY_WS_PATH
import com.wire.bots.polls.setup.EnvConfigVariables.SERVICE_TOKEN
import com.wire.bots.polls.setup.EnvConfigVariables.USE_WEB_SOCKETS
import com.wire.bots.polls.websockets.WebSocketConfig
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
// TODO load all config from the file and then allow the replacement with env variables
fun MainBuilder.bindConfiguration() {

    // The default values used in this configuration are for the local development.

    bind<DatabaseConfiguration>() with singleton {
        DatabaseConfiguration(
            userName = getEnvOrLogDefault(DB_USER, "wire-poll-bot"),
            password = getEnvOrLogDefault(DB_PASSWORD, "super-secret-wire-pwd"),
            url = getEnvOrLogDefault(DB_URL, "jdbc:postgresql://localhost:5432/poll-bot")
        )
    }

    bind<String>("proxy-auth") with singleton {
        getEnvOrLogDefault(SERVICE_TOKEN, "local-token")
    }

    bind<String>("app-key-websocket") with singleton {
        getEnvOrLogDefault(APP_KEY, "")
    }

    bind<Boolean>("use-websocket") with singleton {
        getEnvOrLogDefault(USE_WEB_SOCKETS, "false").toBoolean()
    }

    bind<WebSocketConfig>() with singleton {
        val appKey = instance<String>("app-key-websocket")

        val host = getEnvOrLogDefault(PROXY_WS_HOST, "proxy.services.zinfra.io")
        val path = getEnvOrLogDefault(PROXY_WS_PATH, "/await")

        WebSocketConfig(host = host, path = "$path/$appKey")
    }

    bind<ProxyConfiguration>() with singleton {
        ProxyConfiguration(getEnvOrLogDefault(PROXY_DOMAIN, "https://proxy.services.zinfra.io"))
    }
}
