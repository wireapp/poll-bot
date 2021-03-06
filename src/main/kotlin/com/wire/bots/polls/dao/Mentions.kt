package com.wire.bots.polls.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

/**
 * Table storing votes of the users.
 */
object Mentions : IntIdTable("mentions") {
    /**
     * Id of the poll.
     */
    val pollId: Column<String> = varchar("poll_id", 36) references Polls.id

    /**
     * If of user that is mentioned.
     */
    val userId: Column<String> = varchar("user_id", 36)

    /**
     * Where mention begins.
     */
    val offset: Column<Int> = integer("offset_shift")

    /**
     * Length of the mention.
     */
    val length: Column<Int> = integer("length")
}
