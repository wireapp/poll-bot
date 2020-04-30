package com.wire.bots.polls.utils

import mu.KLogging

/**
 * Creates URL from [this] as base and [path] as path
 */
infix fun String.appendPath(path: String) = "${dropLastWhile { it == '/' }}/${path.dropWhile { it == '/' }}"

/**
 * Creates logger with given name.
 */
fun createLogger(name: String) = KLogging().logger("com.wire.$name")
