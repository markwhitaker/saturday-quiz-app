package uk.co.mainwave.saturdayquizapp.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val PREFS_NAME_DEFAULT = "prefs"
const val PREFS_NAME_SCORES = "scores"

val appModule = module {
    single { provideSharedPreferences(get(), PREFS_NAME_DEFAULT) }
    single(named(PREFS_NAME_SCORES)) {
        provideSharedPreferences(get(), PREFS_NAME_SCORES)
    }
}

private fun provideSharedPreferences(context: Context, name: String): SharedPreferences {
    return context.getSharedPreferences(name, Context.MODE_PRIVATE)
}
