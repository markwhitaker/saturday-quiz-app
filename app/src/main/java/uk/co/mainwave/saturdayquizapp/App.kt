package uk.co.mainwave.saturdayquizapp

import android.app.Application
import uk.co.mainwave.saturdayquizapp.di.ApplicationComponent
import uk.co.mainwave.saturdayquizapp.di.DaggerApplicationComponent

class App : Application() {

    lateinit var component: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        component = DaggerApplicationComponent.builder().build()
    }
}