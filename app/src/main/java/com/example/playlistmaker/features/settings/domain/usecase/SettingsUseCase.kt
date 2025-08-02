package com.example.playlistmaker.features.settings.domain.usecase

import com.example.playlistmaker.features.settings.domain.repository.SettingsRepository

class SettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    fun getThemeSettings(): Boolean {
        return settingsRepository.getThemeSettings()
    }

    fun saveThemeSettings(darkThemeEnabled: Boolean) {
        settingsRepository.saveThemeSettings(darkThemeEnabled)
    }

    fun applyTheme(darkThemeEnabled: Boolean) {
        settingsRepository.applyTheme(darkThemeEnabled)
    }
} 