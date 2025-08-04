package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.features.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.features.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.features.settings.domain.usecase.SettingsUseCase
import com.example.playlistmaker.features.settings.domain.usecase.GetThemeSettingsUseCase
import com.example.playlistmaker.features.settings.domain.usecase.SaveThemeSettingsUseCase
import com.example.playlistmaker.features.settings.presentation.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsModule = module {

    single<SharedPreferences>(qualifier = named("settings_prefs")) {
        androidContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(qualifier = named("settings_prefs")))
    }

    single {
        SettingsUseCase(
            settingsRepository = get()
        )
    }

    single {
        GetThemeSettingsUseCase(
            settingsRepository = get()
        )
    }

    single {
        SaveThemeSettingsUseCase(
            settingsRepository = get()
        )
    }

    viewModel {
        SettingsViewModel(
            settingsUseCase = get()
        )
    }
}
