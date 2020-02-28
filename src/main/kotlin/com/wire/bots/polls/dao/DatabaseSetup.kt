package com.wire.bots.polls.dao

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Object with methods for managing the database.
 */
object DatabaseSetup {

    /**
     * Connect bot to the database via provided connection string.
     *
     * This method does not check whether it is possible to connect to the database, use [isConnected] for that.
     */
    fun connect(connectionString: String) = Database.connect(connectionString, driver = "org.postgresql.Driver")

    /**
     * Returns true if the bot is connected to database
     */
    fun isConnected() = runCatching {
        // execute simple query to verify whether the db is connected
        // if the transaction throws exception, database is not connected
        transaction {
            this.connection.isClosed
        }
    }.isSuccess
}
