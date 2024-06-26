package uk.co.mainwave.saturdayquizapp.di

import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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

private fun provideOkHttp(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor { Log.i("HTTP_LOG", it) }
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addNetworkInterceptor(StethoInterceptor())
        .callTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}

private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
    .baseUrl("https://eaton-bitrot.koyeb.app/api/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
