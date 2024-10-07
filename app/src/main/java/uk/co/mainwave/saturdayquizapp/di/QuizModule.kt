package uk.co.mainwave.saturdayquizapp.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import uk.co.mainwave.saturdayquizapp.repository.PrefsRepository
import uk.co.mainwave.saturdayquizapp.repository.QuizRepository
import uk.co.mainwave.saturdayquizapp.viewmodel.QuizViewModel

val quizModule = module {
    factoryOf(::QuizRepository)
    factoryOf(::PrefsRepository)
    viewModelOf(::QuizViewModel)
}
