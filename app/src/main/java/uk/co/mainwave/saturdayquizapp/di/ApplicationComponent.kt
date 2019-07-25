package uk.co.mainwave.saturdayquizapp.di

import dagger.Component
import uk.co.mainwave.saturdayquizapp.QuizActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetworkModule::class
])
interface ApplicationComponent {
    fun inject(quizActivity: QuizActivity)
}