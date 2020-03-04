package com.wire.bots.polls.dao

import ai.blindspot.ktoolz.extensions.mapToSet
import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.bots.polls.dto.PollAction
import com.wire.bots.polls.dto.PollDto
import mu.KLogging
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
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
     * [IllegalArgumentException] is thrown.
     */
    suspend fun vote(pollAction: PollAction) = newSuspendedTransaction {
        val voteId = PollOptions.select {
                (PollOptions.pollId eq pollAction.pollId) and (PollOptions.optionOrder eq pollAction.optionId)
            }.singleOrNull()
            ?.getOrNull(PollOptions.id)
            ?.value
            .whenNull {
                logger.error { "There is no poll which has id ${pollAction.pollId} or no option with order ${pollAction.optionId}" }
            } ?: throw IllegalArgumentException("No poll or option found for the received vote!")


        Votes.insert {
            it[pollOption] = voteId
            it[userId] = pollAction.userId
        }
    }

    /**
     * Retrieves stats for given pollId.
     *
     * Offset/option id/button as keys and user ids as values.
     */
    suspend fun stats(pollId: String) = newSuspendedTransaction {
        (PollOptions leftJoin Votes)
            .slice(PollOptions.optionOrder, Votes.userId)
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
        (PollOptions rightJoin Votes)
            .slice(Votes.userId)
            .select {
                PollOptions.pollId eq pollId
            }
            .mapToSet { it[Votes.userId] }
    }
}
