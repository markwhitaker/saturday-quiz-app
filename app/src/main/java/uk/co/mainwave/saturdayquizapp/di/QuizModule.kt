package uk.co.mainwave.saturdayquizapp.di

import org.koin.dsl.module
import uk.co.mainwave.saturdayquizapp.PreferencesRepository
import uk.co.mainwave.saturdayquizapp.QuizPresenter
import uk.co.mainwave.saturdayquizapp.QuizRepository

val quizModule = module {
    factory { QuizPresenter(get(), get()) }
    factory { QuizRepository(get()) }
    factory { PreferencesRepository(get()) }
}
