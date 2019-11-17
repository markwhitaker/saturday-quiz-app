package uk.co.mainwave.saturdayquizapp.di

import org.koin.dsl.module
import uk.co.mainwave.saturdayquizapp.presenter.QuizPresenter
import uk.co.mainwave.saturdayquizapp.repository.PreferencesRepository
import uk.co.mainwave.saturdayquizapp.repository.QuizRepository

val quizModule = module {
    factory { QuizPresenter(get(), get()) }
    factory { QuizRepository(get()) }
    factory { PreferencesRepository(get()) }
}
