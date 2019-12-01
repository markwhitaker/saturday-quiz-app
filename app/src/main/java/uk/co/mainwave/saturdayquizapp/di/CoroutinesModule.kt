package uk.co.mainwave.saturdayquizapp.di

import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

val coroutinesModule = module {
    single<CoroutineContext>(named(UI_DISPATCHER_NAME)) { Dispatchers.Main }
}

const val UI_DISPATCHER_NAME = "UI dispatcher"
