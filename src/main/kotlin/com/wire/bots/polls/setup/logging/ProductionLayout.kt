package com.wire.bots.polls.setup.logging


import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.CoreConstants
import ch.qos.logback.core.LayoutBase
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Layout logging into jsons.
 */
class ProductionLayout : LayoutBase<ILoggingEvent>() {

    private companion object {
        val dateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ISO_DATE_TIME
                .withZone(ZoneOffset.UTC)
    }

    override fun doLayout(event: ILoggingEvent): String =
        with(StringBuffer(256)) {
            append("{")
            appendJson("@timestamp", getTime())

            event.mdcPropertyMap[INFRA_REQUEST]?.let {
                appendJson("infra_request", it)
            }

            event.mdcPropertyMap[APP_REQUEST]?.let {
                appendJson("app_request", it)
            }

            appendJson("logger", event.loggerName)
            appendJson("message", event.formattedMessage)
            appendJson("level", event.level.levelStr)
            appendJson("thread_name", event.threadName, "")
            append("}")
            append(CoreConstants.LINE_SEPARATOR)
            toString()
        }

    private fun StringBuffer.appendJson(key: String, value: String, ending: String = ","): StringBuffer =
        append("\"$key\":\"$value\"$ending")

    private fun getTime(): String = dateTimeFormatter.format(Instant.now())
}
