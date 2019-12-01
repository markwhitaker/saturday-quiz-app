package uk.co.mainwave.saturdayquizapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import uk.co.mainwave.saturdayquizapp.di.appModule
import uk.co.mainwave.saturdayquizapp.di.coroutinesModule
import uk.co.mainwave.saturdayquizapp.di.networkModule
import uk.co.mainwave.saturdayquizapp.di.quizModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(
                appModule,
                coroutinesModule,
                networkModule,
                quizModule
            ))
        }
    }
}
