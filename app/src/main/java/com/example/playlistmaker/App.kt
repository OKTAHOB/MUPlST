package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.appModule
import com.example.playlistmaker.features.settings.domain.usecase.SettingsUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class App : Application(), KoinComponent {

    private val settingsUseCase: SettingsUseCase by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }

        val darkTheme = settingsUseCase.getThemeSettings()
        applyTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        settingsUseCase.saveThemeSettings(darkThemeEnabled)
        settingsUseCase.applyTheme(darkThemeEnabled)
    }

    private fun applyTheme(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
