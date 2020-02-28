# Wire Poll Bot
[Wire](https://wire.com/) bot for the polls.

## Dev Stack
* HTTP Server - [Ktor](https://ktor.io/)
* HTTP Client - [CIO](https://ktor.io/clients/http-client/engines.html) under [Ktor](https://ktor.io/)
* Dependency Injection - [Kodein](https://github.com/Kodein-Framework/Kodein-DI)
* Build system - [Gradle](https://gradle.org/)
* Communication with [Wire](https://wire.com/) - [Roman](https://github.com/dkovacevic/roman)

## Usage
* To run the application simply execute `make run` or `./gradlew run`.
* To run the application inside the docker run `make docker-build && make docker-run`

For more details see [Makefile](Makefile).

## Docker Hub
Poll Bot has docker image available on [Docker Hub](https://hub.docker.com/r/lukaswire/polls).
```bash
docker pull lukaswire/polls:<version>
```

## Bot configuration
Configuration is currently being loaded from the environment variables.

```kotlin
    /**
     * Connection string for the database that includes username and password.
     *
     * Must be in the format with user and password:
     * `jdbc:postgresql://<address>:<port>/<db-name>?user=<username>&password=<password>`
     * For example:
     * `jdbc:postgresql://localhost:5432/bot-database?user=cool-user&password=super-secret-db-password`
     */
    const val DB_CONNECTION_STRING = "DB_CONNECTION_STRING"
    /**
     * Id of the current bot.
     */
    const val SERVICE_CODE = "SERVICE_CODE"
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
    /**
     * Host name for the connection to web socket eg."proxy.services.zinfra.io"
     */
    const val PROXY_WS_HOST = "PROXY_WS_HOST"
    /**
     * Path to web socket at proxy eg. "/await"
     */
    const val PROXY_WS_PATH = "PROXY_WS_PATH"
    /**
     * Domain used for sending the messages from the bot to proxy eg. "https://proxy.services.zinfra.io"
     */
    const val PROXY_DOMAIN = "PROXY_DOMAIN"
```

Via the system variables - see [complete list](src/main/kotlin/com/wire/bots/polls/setup/EnvConfigVariables.kt).

## Docker Compose
To run bot inside docker compose environment with default PostgreSQL database,
please create `.env` file in the root directory with the following variables:
```bash
# database
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_DB=

# application
DB_CONNECTION_STRING=
SERVICE_CODE=
SERVICE_TOKEN=
APP_KEY=
USE_WEB_SOCKETS=
PROXY_WS_HOST=
PROXY_WS_PATH=
PROXY_DOMAIN=
```
