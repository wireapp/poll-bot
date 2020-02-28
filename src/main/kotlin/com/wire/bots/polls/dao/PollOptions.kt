package com.wire.bots.polls.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

/**
 * Poll options.
 */
object PollOptions : IntIdTable("poll_option") {

    /**
     * Id of the poll this option is for. UUID.
     */
    val pollId: Column<String> = varchar("poll_id", 36) references Polls.id

    /**
     * Option order or option id.
     */
    val optionOrder: Column<Int> = integer("option_order")

    /**
     * Option content, the text inside the button/choice.
     */
    val optionContent: Column<String> = varchar("option_content", 256)
}
