package com.wire.bots.polls.setup

import com.wire.bots.polls.services.ProxyConfiguration
import com.wire.bots.polls.setup.EnvConfigVariables.APP_KEY
import com.wire.bots.polls.setup.EnvConfigVariables.PROXY_DOMAIN
import com.wire.bots.polls.setup.EnvConfigVariables.PROXY_WS_HOST
import com.wire.bots.polls.setup.EnvConfigVariables.PROXY_WS_PATH
import com.wire.bots.polls.setup.EnvConfigVariables.SERVICE_TOKEN
import com.wire.bots.polls.websockets.WebSocketConfig
import org.kodein.di.Kodein.MainBuilder
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

/**
 * Loads the DI container with configuration from the system environment.
 */
// TODO load all config from the file and then allow the replacement with env variables
fun MainBuilder.bindConfiguration() {

    bind<String>("proxy-auth") with singleton {
        System.getenv(SERVICE_TOKEN) ?: ""
    }

    bind<String>("app-key-websocket") with singleton {
        System.getenv(APP_KEY) ?: ""
    }

    bind<Boolean>("use-websocket") with singleton { false }

    bind<WebSocketConfig>() with singleton {
        val appKey = instance<String>("app-key-websocket")

        val host = System.getenv(PROXY_WS_HOST) ?: "proxy.services.zinfra.io"
        val path = System.getenv(PROXY_WS_PATH) ?: "/await"

        WebSocketConfig(host = host, path = "$path/$appKey")
    }

    bind<ProxyConfiguration>() with singleton {
        ProxyConfiguration(System.getenv(PROXY_DOMAIN) ?: "https://proxy.services.zinfra.io")
    }
}
