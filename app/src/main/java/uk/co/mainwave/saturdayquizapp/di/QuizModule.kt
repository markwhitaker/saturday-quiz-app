package uk.co.mainwave.saturdayquizapp.di

import org.koin.dsl.module
import uk.co.mainwave.saturdayquizapp.QuizPresenter
import uk.co.mainwave.saturdayquizapp.QuizRepository

val quizModule = module {
    factory { QuizPresenter(get()) }
    factory { QuizRepository(get()) }
}
