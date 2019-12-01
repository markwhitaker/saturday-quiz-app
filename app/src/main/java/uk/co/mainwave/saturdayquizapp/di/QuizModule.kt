package uk.co.mainwave.saturdayquizapp.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import uk.co.mainwave.saturdayquizapp.presenter.QuizPresenter
import uk.co.mainwave.saturdayquizapp.repository.PreferencesRepository
import uk.co.mainwave.saturdayquizapp.repository.QuizRepository

val quizModule = module {
    factory { QuizPresenter(get(), get(), get(named(UI_DISPATCHER_NAME))) }
    factory { QuizRepository(get()) }
    factory { PreferencesRepository(get()) }
}
