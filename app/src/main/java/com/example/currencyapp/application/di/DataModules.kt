package com.example.currencyapp.application.di

import android.preference.PreferenceManager
import com.example.currencyapp.data.prefs.PreferencesService
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

val databaseModule: Module = module {

}

val preferencesModule: Module = module {
    single { PreferenceManager.getDefaultSharedPreferences(androidApplication()) }
    single { PreferencesService(androidApplication(), get(), get()) }
}
