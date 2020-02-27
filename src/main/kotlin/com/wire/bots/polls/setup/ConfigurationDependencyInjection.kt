package com.wire.bots.polls.setup

import com.wire.bots.polls.services.ProxyConfiguration
import com.wire.bots.polls.websockets.WebSocketConfig
import org.kodein.di.Kodein.MainBuilder
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

fun MainBuilder.bindConfiguration() {

    // TODO replace this with DB fetch : service_token
    bind<String>("proxy-auth") with singleton {
        System.getenv("SERVICE_TOKEN") ?: ""
    }

    // TODO load this from the configuration file
    bind<String>("app-key-websocket") with singleton {
        System.getenv("APP_KEY") ?: ""
    }

    // TODO load from the env
    bind<Boolean>("use-websocket") with singleton { false }

    // TODO document it
    bind<WebSocketConfig>() with singleton {
        val appKey = instance<String>("app-key-websocket")
        val host = System.getenv("PROXY_WS_HOST") ?: "proxy.services.zinfra.io"

        WebSocketConfig(host = host, path = "/await/$appKey")
    }

    bind<ProxyConfiguration>() with singleton {
        ProxyConfiguration(System.getenv("PROXY_DOMAIN") ?: "https://proxy.services.zinfra.io")
    }
}
