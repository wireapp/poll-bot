package com.wire.bots.polls.exceptions

class NotSupportedTypeException(val givenType: String, val expectedTypes: Collection<String>) : Exception(
    "Given type: $givenType could not be processed. Expected types are: ${expectedTypes.joinToString(", ")}."
)
