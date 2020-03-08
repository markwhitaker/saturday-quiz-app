package uk.co.mainwave.saturdayquizapp.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.dsl.module

val appModule = module {
    single { provideSharedPreferences(get()) }
}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
}
