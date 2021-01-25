package com.example.currencyapp.application.di

import com.example.screens.main.MainActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelsModule: Module = module {

    viewModel { MainActivityViewModel(get(), get(), get(), get()) }
}