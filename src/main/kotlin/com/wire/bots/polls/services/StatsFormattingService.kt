package com.wire.bots.polls.services

import ai.blindspot.ktoolz.extensions.newLine
import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.bots.polls.dao.PollRepository
import com.wire.bots.polls.dto.bot.BotMessage
import com.wire.bots.polls.dto.bot.statsMessage
import mu.KLogging

class StatsFormattingService(
    private val repository: PollRepository
) {
    private companion object : KLogging() {
        const val titlePrefix = "**Results** for poll *\""
    }

    /**
     * Prepares message with statistics about the poll to the proxy.
     */
    suspend fun formatStats(pollId: String): BotMessage? {
        val pollQuestion = repository.getPollQuestion(pollId).whenNull {
            logger.warn { "No poll $pollId exists." }
        } ?: return null

        val stats = repository.stats(pollId)
        if (stats.isEmpty()) {
            logger.info { "There are no data for given pollId." }
            return null
        }

        val title = prepareTitle(pollQuestion.body)
        val options = formatVotes(stats)
        return statsMessage(
            text = "$title$newLine$options",
            mentions = pollQuestion.mentions.map { it.copy(offset = it.offset + titlePrefix.length) }
        )
    }

    private fun formatVotes(stats: Map<String, Int>): String {
        // we can use assert as the result size is checked
        val maxVotes = stats.values.max()!!
        return stats
            .map { (option, votingUsers) ->
                VotingOption(if (votingUsers == maxVotes) "**" else "*", option, votingUsers)
            }.let { votes ->
                votes.joinToString(newLine) { it.toString(maxVotes) }
            }
    }

    private fun prepareTitle(body: String) = "$titlePrefix${body}\"*"

}

/**
 * Class used for formatting voting objects.
 */
private data class VotingOption(val style: String, val option: String, val votingUsers: Int) {

    private companion object {
        const val notVote = "\uD83D\uDD34"
        const val vote = "\uD83D\uDD35"
    }

    fun toString(max: Int): String {
        val missingVotes = (0 until max - votingUsers).joinToString("") { notVote }
        val votes = (0 until votingUsers).joinToString("") { vote }
        return "$votes$missingVotes $style$option$style ($votingUsers)"
    }
}

