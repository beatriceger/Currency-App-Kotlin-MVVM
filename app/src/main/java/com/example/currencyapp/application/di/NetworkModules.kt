package com.example.currencyapp.application.di

import com.example.currencyapp.data.api.apis.CurrencyApi
import com.example.currencyapp.domain.util.rx.AppRxSchedulers
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.example.currencyapp.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File


val networkModule: Module = module {
    single {
        HttpLoggingInterceptor { Timber.i(it) }
            .setLevel(
                HttpLoggingInterceptor.Level.BODY
            )
    }
    single { Cache(File(androidContext().filesDir, "OkHttpCache"), 10 * 1024 * 1024) }
    single { provideOkHttpClient(get(), get()) }
}

val restModule: Module = module {
    single { provideRetrofit(get(), get(), get()) }
    single {
        val retrofit: Retrofit = get()
        retrofit.create(CurrencyApi::class.java)

    }
}

fun provideOkHttpClient(
    cache: Cache,
    httpLoggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    return OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(httpLoggingInterceptor)
        .build()
}

fun provideRetrofit(appRxSchedulers: AppRxSchedulers, gson: Gson, client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(appRxSchedulers.network()))
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BuildConfig.API_URL)
        .client(client)
        .build()
}
