package com.example.playlistmaker.domain.usecase

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.usecase.interactor.SettingsInteractor

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {

    override fun getThemeSettings(): Boolean = settingsRepository.getThemeSettings()

    override fun saveThemeSettings(darkThemeEnabled: Boolean) {
        settingsRepository.saveThemeSettings(darkThemeEnabled)
    }

    override fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}