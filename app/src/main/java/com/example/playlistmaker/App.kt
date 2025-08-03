package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.presentation.util.Creator

class App : Application() {
    private lateinit var getThemeSettingsUseCase: com.example.playlistmaker.features.settings.domain.usecase.GetThemeSettingsUseCase
    private lateinit var saveThemeSettingsUseCase: com.example.playlistmaker.features.settings.domain.usecase.SaveThemeSettingsUseCase

    override fun onCreate() {
        super.onCreate()
        getThemeSettingsUseCase = Creator.provideGetThemeSettingsUseCase(this)
        saveThemeSettingsUseCase = Creator.provideSaveThemeSettingsUseCase(this)

        val darkTheme = getThemeSettingsUseCase.execute()
        applyTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        saveThemeSettingsUseCase.execute(darkThemeEnabled)
    }

    private fun applyTheme(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}