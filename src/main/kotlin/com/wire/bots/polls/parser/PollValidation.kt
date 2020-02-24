package com.wire.bots.polls.parser

import com.wire.bots.polls.dto.Option
import com.wire.bots.polls.dto.PollDto
import com.wire.bots.polls.dto.Question
import mu.KLogging

typealias QuestionRule = (Question) -> String?
typealias OptionRule = (Option) -> String?
typealias PollRule = (PollDto) -> String?

class PollValidation {

    private companion object : KLogging()

    private val questionRules = listOf<QuestionRule> { question -> if (question.isNotBlank()) null else "The question must not be empty!" }

    private val optionRules = listOf<OptionRule> { option -> if (option.isNotBlank()) null else "The option must not be empty!" }

    private val pollRules = listOf<PollRule> { poll -> if (poll.options.isNotEmpty()) null else "There must be at least one option for answering the poll." }

    /**
     * Validates given poll, returns pair when the boolean signalizes whether the poll is valid or not.
     *
     * The string collection contains violated constraints when the poll is invalid.
     */
    fun validate(poll: PollDto): Pair<Boolean, Collection<String>> {
        val pollValidation = pollRules.mapNotNull { it(poll) }
        val questionValidation = questionRules.mapNotNull { it(poll.question) }
        val optionsValidation = optionRules.flatMap { poll.options.mapNotNull(it) }

        return (pollValidation.isEmpty() && questionValidation.isEmpty() && optionsValidation.isEmpty()) to pollValidation + questionValidation + optionsValidation
    }
}
