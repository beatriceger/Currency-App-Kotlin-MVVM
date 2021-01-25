package com.example.currencyapp.application.di

import com.example.currencyapp.domain.util.rx.AppRxSchedulers
import com.example.currencyapp.domain.util.rx.RxBus
import com.fatboyindustrial.gsonjodatime.Converters
import com.google.gson.GsonBuilder
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.module.Module
import org.koin.dsl.module


val gsonModule: Module = module {
    single {
        val builder = GsonBuilder()
        Converters.registerAll(builder)
        builder.create()
    }
}

val rxModule: Module = module {
    single { AppRxSchedulers() }
    single { RxBus() }
    factory { CompositeDisposable() }
}