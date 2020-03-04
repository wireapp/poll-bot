package com.wire.bots.polls.integration_tests.setup

/**
 * Contains variables that are loaded from the environment.
 */
object EnvConfigVariables {
    /**
     * Token which is used for the auth of proxy.
     */
    const val SERVICE_TOKEN = "SERVICE_TOKEN"

    /**
     * Key for connecting to the web socket of the proxy.
     */
    const val APP_KEY = "APP_KEY"

    /**
     * Determines whether to use web sockets for connection to proxy or not eg. true
     */
    const val USE_WEB_SOCKETS = "USE_WEB_SOCKETS"
}
