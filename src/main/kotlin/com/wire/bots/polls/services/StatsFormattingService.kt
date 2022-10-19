package com.wire.bots.polls.services

import com.wire.bots.polls.dao.PollRepository
import com.wire.bots.polls.dto.bot.BotMessage
import com.wire.bots.polls.dto.bot.statsMessage
import mu.KLogging
import pw.forst.katlib.newLine
import pw.forst.katlib.whenNull
import kotlin.math.min

class StatsFormattingService(
    private val repository: PollRepository
) {
    private companion object : KLogging() {
        const val titlePrefix = "**Results** for poll *\""

        /**
         * Maximum number of trailing vote slots to be displayed, considered the most voted option.
         */
        const val MAX_VOTE_PLACEHOLDER_COUNT = 2
    }

    /**
     * Prepares message with statistics about the poll to the proxy.
     * When conversationMembers is null, stats are formatted according to the max votes per option.
     */
    suspend fun formatStats(pollId: String, conversationMembers: Int?): BotMessage? {
        val pollQuestion = repository.getPollQuestion(pollId).whenNull {
            logger.warn { "No poll $pollId exists." }
        } ?: return null

        val stats = repository.stats(pollId)
        if (stats.isEmpty()) {
            logger.info { "There are no data for given pollId." }
            return null
        }

        val title = prepareTitle(pollQuestion.body)
        val options = formatVotes(stats, conversationMembers)
        return statsMessage(
            text = "$title$newLine$options",
            mentions = pollQuestion.mentions.map { it.copy(offset = it.offset + titlePrefix.length) }
        )
    }

    /**
     * Formats the vote results using the most voted option to determine the output size.
     * Will add [MAX_VOTE_PLACEHOLDER_COUNT] number of trailing placeholders to
     * until it reaches [conversationMembers].
     *
     * Examples:
     * With [MAX_VOTE_PLACEHOLDER_COUNT] = 2 and [conversationMembers] >= 5:
     * - ⬛⬜⬜⬜⬜ A (1)
     * - ⬛⬛⬛⬜⬜ B (3)
     * - ⬛⬛⬜⬜⬜ C (2)
     *
     * With [MAX_VOTE_PLACEHOLDER_COUNT] = 2 and 4 [conversationMembers] = 4:
     * - ⬜⬜⬜⬜ A (0)
     * - ⬛⬛⬛⬜ B (3)
     * - ⬛⬜⬜⬜ C (1)
     *
     * With [MAX_VOTE_PLACEHOLDER_COUNT] = 2 and 3 [conversationMembers] = 3:
     * - ⬛⬛⬛ A (3)
     * - ⬜⬜⬜ B (1)
     */
    private fun formatVotes(stats: Map<Pair<Int, String>, Int>, conversationMembers: Int?): String {
        // we can use assert as the result size is checked
        val mostPopularOptionVoteCount = requireNotNull(stats.values.maxOrNull()) { "There were no stats!" }

        val maximumSize = min(
            conversationMembers ?: Integer.MAX_VALUE,
            mostPopularOptionVoteCount + MAX_VOTE_PLACEHOLDER_COUNT
        )

        return stats
            .map { (option, votingUsers) ->
                VotingOption(if (votingUsers == mostPopularOptionVoteCount) "**" else "*", option.second, votingUsers)
            }.let { votes ->
                votes.joinToString(newLine) { it.toString(maximumSize) }
            }
    }

    private fun prepareTitle(body: String) = "$titlePrefix${body}\"*"

}

/**
 * Class used for formatting voting objects.
 */
private data class VotingOption(val style: String, val option: String, val votingUsers: Int) {

    private companion object {
        const val notVote = "⬜"
        const val vote = "⬛"
    }

    fun toString(max: Int): String {
        val missingVotes = (0 until max - votingUsers).joinToString("") { notVote }
        val votes = (0 until votingUsers).joinToString("") { vote }
        return "$votes$missingVotes $style$option$style ($votingUsers)"
    }
}

