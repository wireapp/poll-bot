package com.wire.bots.polls.setup.logging


import ai.blindspot.ktoolz.extensions.createJson
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.classic.spi.ThrowableProxyUtil
import ch.qos.logback.core.CoreConstants
import ch.qos.logback.core.LayoutBase
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


/**
 * Layout logging into jsons.
 */
class JsonLoggingLayout : LayoutBase<ILoggingEvent>() {

    private companion object {
        val dateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ISO_DATE_TIME
                .withZone(ZoneOffset.UTC)
    }

    override fun doLayout(event: ILoggingEvent): String =
        with(StringBuffer(256)) {
            append("{")
            appendJson("@timestamp", formatTime(event))

            event.mdcPropertyMap[INFRA_REQUEST]?.let {
                appendJson("infra_request", it)
            }

            event.mdcPropertyMap[APP_REQUEST]?.let {
                appendJson("app_request", it)
            }

            appendJson("logger", event.loggerName)
            appendJson("message", event.formattedMessage)
            appendJson("level", event.level.levelStr)
            appendJson("thread_name", event.threadName)

            appendException(event.throwableProxy)

            append("}")
            append(CoreConstants.LINE_SEPARATOR)
            toString()
        }

    private fun StringBuffer.appendException(proxy: IThrowableProxy?) {
        if (proxy == null) return
        val json = createJson(
            mapOf(
                "message" to proxy.message,
                "class" to proxy.className,
                "stacktrace" to ThrowableProxyUtil.asString(proxy)
            )
        )
        append("\"exception\":$json")
    }

    private fun StringBuffer.appendJson(key: String, value: String, ending: String = ","): StringBuffer =
        append("\"$key\":\"$value\"$ending")

    private fun formatTime(event: ILoggingEvent): String =
        dateTimeFormatter.format(Instant.ofEpochMilli(event.timeStamp))
}
