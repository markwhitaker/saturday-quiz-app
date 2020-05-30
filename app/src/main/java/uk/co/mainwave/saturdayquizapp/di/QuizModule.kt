package uk.co.mainwave.saturdayquizapp.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import uk.co.mainwave.saturdayquizapp.repository.QuizRepository
import uk.co.mainwave.saturdayquizapp.repository.PrefsRepository
import uk.co.mainwave.saturdayquizapp.viewmodel.QuizViewModel

val quizModule = module {
    factory { QuizRepository(get()) }
    factory { PrefsRepository(get()) }
    viewModel { QuizViewModel(get(), get()) }
}
