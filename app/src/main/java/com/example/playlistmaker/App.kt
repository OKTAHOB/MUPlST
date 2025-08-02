package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.preferences.SettingsRepositoryImpl

class App : Application() {
    private lateinit var settingsRepository: SettingsRepositoryImpl

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepositoryImpl(
            getSharedPreferences("settings", MODE_PRIVATE)
        )
        val darkTheme = settingsRepository.getThemeSettings()
        applyTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        settingsRepository.saveThemeSettings(darkThemeEnabled)
        applyTheme(darkThemeEnabled)
    }

    private fun applyTheme(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}