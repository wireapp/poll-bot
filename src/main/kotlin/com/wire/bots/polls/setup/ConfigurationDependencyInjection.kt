package com.wire.bots.polls.setup

import com.wire.bots.polls.services.ProxyConfiguration
import com.wire.bots.polls.websockets.WebSocketConfig
import org.kodein.di.Kodein.MainBuilder
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

fun MainBuilder.bindConfiguration() {

    // TODO replace this with DB fetch
    bind<String>("proxy-auth") with singleton { "token" }


//    bind<WebSocketConfig>() with singleton {
//        WebSocketConfig(host = "127.0.0.1", port = 1234, path = "")
//    }

    bind<WebSocketConfig>() with singleton {
        val appKey = instance<String>("app-key-websocket")
        WebSocketConfig(host = "proxy.services.zinfra.io", path = "/await/$appKey")
    }

    bind<ProxyConfiguration>() with singleton { ProxyConfiguration("https://proxy.services.zinfra.io") }
}
