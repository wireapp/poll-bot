package com.wire.bots.polls.dao

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

/**
 * Table storing votes of the users.
 */
object Votes : Table("votes") {
    /**
     * Id of the option.
     */
    val pollOption: Column<Int> = integer("poll_option").references(PollOptions.id)

    /**
     * User who voted for this option.
     */
    val userId: Column<String> = varchar("user_id", 36)

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(pollOption, userId)
}
