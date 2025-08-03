package com.example.playlistmaker.features.settings.domain.usecase

import com.example.playlistmaker.features.settings.domain.repository.SettingsRepository

class SaveThemeSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    fun execute(darkThemeEnabled: Boolean) {
        settingsRepository.saveThemeSettings(darkThemeEnabled)
        settingsRepository.applyTheme(darkThemeEnabled)
    }
}