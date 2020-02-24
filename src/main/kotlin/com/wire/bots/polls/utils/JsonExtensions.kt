package com.wire.bots.polls.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KLogging

/**
 * Logger for this file.
 */
@PublishedApi
internal val jsonLogger = KLogging().logger("JsonExtensions")

/**
 * Standard [ObjectMapper] configured in a way the platform operates.
 */
fun jacksonMapper(): ObjectMapper = jacksonObjectMapper().apply {
    configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false)
}

/**
 * Tries to create instance of T from provided [json], null is returned when it is not possible to parse it.
 */
inline fun <reified T> parseJson(json: String): T? =
    runCatching { jacksonMapper().readValue<T>(json) }
        .onFailure { jsonLogger.warn(it) { "Exception raised during JSON parsing:${System.lineSeparator()}$json" } }
        .getOrNull()

/**
 * Tries to create instance of T from provided [json], null is returned when it is not possible to parse it.
 */
inline fun <reified T> parseJson(json: ByteArray): T? =
    runCatching { jacksonMapper().readValue<T>(json) }
        .onFailure { jsonLogger.warn(it) { "Exception raised during JSON parsing:${System.lineSeparator()}$json" } }
        .getOrNull()

/**
 * Serializes given object to string.
 */
fun <T : Any> createJson(value: T): String = jacksonMapper().writeValueAsString(value)

/**
 * Serializes given object to byte array.
 */
fun <T : Any> createJsonBytes(value: T): ByteArray = jacksonMapper().writeValueAsBytes(value)

/**
 * Pretty print a json.
 */
fun prettyPrintJson(json: String): String = with(jacksonMapper()) {
    writerWithDefaultPrettyPrinter().writeValueAsString(readValue<Any>(json))
}
