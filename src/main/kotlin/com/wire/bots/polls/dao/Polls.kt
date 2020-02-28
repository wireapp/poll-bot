package com.wire.bots.polls.dao

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

/**
 * Polls table definition.
 */
object Polls : Table("polls") {
    /**
     * Id of the poll. UUID.
     */
    val id: Column<String> = varchar("id", 36).uniqueIndex()
    /**
     * Id of the conversation this poll was created in. UUID.
     */
    val conversationId: Column<String> = varchar("conversation_id", 36)

    /**
     * Id of the user who created this poll. UUID.
     */
    val ownerId: Column<String> = varchar("owner_id", 36)

    /**
     * Determines whether is the pool active and whether new votes should be accepted.
     */
    val isActive: Column<Boolean> = bool("is_active")

    /**
     * Contains body of the poll - the question.
     */
    val body: Column<String> = text("body", collate = null)

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(id)
}
