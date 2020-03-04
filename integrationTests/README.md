# Wire Poll Bot Integration tests
[Wire](https://wire.com/) bot for the polls - integration test.

This test structure assumes that the bot is running somewhere and hooks are pointed to this API.


## Environment
```kotlin
    /**
     * Bot API URL.
     */
    const val BOT_API = "BOT_API"

    /**
     * Token which is used for the auth of proxy.
     */
    const val SERVICE_TOKEN = "SERVICE_TOKEN"
```

## Dev Stack
* HTTP Server - [Ktor](https://ktor.io/)
* HTTP Client - [CIO](https://ktor.io/clients/http-client/engines.html) under [Ktor](https://ktor.io/)
* Dependency Injection - [Kodein](https://github.com/Kodein-Framework/Kodein-DI)
* Build system - [Gradle](https://gradle.org/)
