package com.wire.bots.polls.setup

import com.wire.bots.polls.filters.TypeFilter
import com.wire.bots.polls.parser.InputParser
import com.wire.bots.polls.parser.InputValidation
import com.wire.bots.polls.parser.PollFactory
import com.wire.bots.polls.parser.PollValidation
import com.wire.bots.polls.services.PollService
import io.ktor.application.Application
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import org.kodein.di.ktor.kodein

fun Application.setupKodein() {
    kodein {
        bind<TypeFilter>() with singleton { TypeFilter() }

        bind<InputValidation>() with singleton { InputValidation() }
        bind<PollValidation>() with singleton { PollValidation() }


        bind<InputParser>() with singleton { InputParser(instance()) }

        bind<PollFactory>() with singleton { PollFactory(instance(), instance()) }
        bind<PollService>() with provider { PollService(instance()) }
    }
}
