package uk.co.mainwave.saturdayquizapp.di

import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    // OkHttp
    single { provideOkHttp() }
    // Retrofit
    single { provideRetrofit(get()) }
}

private fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
    .callTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()

private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
    .baseUrl("https://saturday-quiz-api.herokuapp.com/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
