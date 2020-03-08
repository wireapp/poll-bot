package com.wire.bots.polls.dao

import ai.blindspot.ktoolz.extensions.mapToSet
import com.wire.bots.polls.dto.PollAction
import com.wire.bots.polls.dto.PollDto
import mu.KLogging
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import pw.forst.exposed.insertOrUpdate
import java.util.UUID

/**
 * Simple repository for handling database transactions on one place.
 */
class PollRepository {

    private companion object : KLogging()

    /**
     * Saves given poll to database and returns its id (same as the [pollId] parameter,
     * but this design supports fluent style in the services.
     */
    suspend fun savePoll(poll: PollDto, pollId: UUID, userId: String) = newSuspendedTransaction {
        Polls.insert {
            it[id] = pollId.toString()
            it[ownerId] = userId
            it[isActive] = true
            it[body] = poll.question
        }

        poll.options.forEachIndexed { index, option ->
            PollOptions.insert {
                it[this.pollId] = pollId.toString()
                it[optionOrder] = index
                it[optionContent] = option
            }
        }

        pollId
    }

    /**
     * Register new vote to the poll. If the poll with provided pollId does not exist,
     * database contains foreign key to an option and poll so the SQL exception is thrown.
     */
    suspend fun vote(pollAction: PollAction) = newSuspendedTransaction {
        Votes.insertOrUpdate(Votes.pollId, Votes.userId) {
            it[pollId] = pollAction.pollId
            it[pollOption] = pollAction.optionId
            it[userId] = pollAction.userId
        }
    }

    /**
     * Retrieves stats for given pollId.
     *
     * Offset/option id/button as keys and user ids as values.
     */
    suspend fun stats(pollId: String) = newSuspendedTransaction {
        PollOptions.join(Votes, JoinType.LEFT,
            additionalConstraint = {
                (Votes.pollId eq PollOptions.pollId) and (Votes.pollOption eq PollOptions.optionOrder)
            }
        ).slice(PollOptions.optionOrder, Votes.userId)
            .select {
                PollOptions.pollId eq pollId
            }
            .groupBy({ it[PollOptions.optionOrder] }, { it.getOrNull(Votes.userId) }) // left join so userId can be null
            .mapValues { (_, votingUsers) ->
                votingUsers.filterNotNull()
            }
    }

    /**
     * Returns set of user ids that voted in the poll with given pollId.
     */
    suspend fun votingUsers(pollId: String) = newSuspendedTransaction {
        Votes
            .slice(Votes.userId)
            .select {
                Votes.pollId eq pollId
            }
            .mapToSet { it[Votes.userId] }
    }
}
