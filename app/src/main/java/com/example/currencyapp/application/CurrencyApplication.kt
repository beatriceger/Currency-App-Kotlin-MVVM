package com.example.currencyapp.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.currencyapp.application.di.*
import com.example.screens.main.MainActivity
import com.facebook.stetho.Stetho
import com.github.anrwatchdog.ANRWatchDog
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.example.currencyapp.BuildConfig
import com.example.currencyapp.domain.util.timber.ReleaseTree
import timber.log.Timber


class CurrencyApplication : Application() {

    lateinit var navigationContext: MainActivity

    override fun onCreate() {
        super.onCreate()

        app = this

        startKoin {
            androidContext(this@CurrencyApplication)
            modules(
                listOf(
                    viewModelsModule,
                    gsonModule,
                    rxModule,
                    networkModule,
                    restModule,
                    databaseModule,
                    preferencesModule
                )
            )
        }

        initTimber()
        initStetho()
        ANRWatchDog().start()

        Stetho.initializeWithDefaults(this)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
        else
            Timber.plant(ReleaseTree())
    }

    private fun initStetho(){
        Stetho.initializeWithDefaults(this)
    }

    fun isActivityInitialized() = ::navigationContext.isInitialized

    companion object {
        lateinit var app: CurrencyApplication
    }
}