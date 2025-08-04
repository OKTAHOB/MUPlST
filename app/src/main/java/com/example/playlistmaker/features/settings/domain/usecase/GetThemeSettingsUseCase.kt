package com.example.playlistmaker.features.settings.domain.usecase

import com.example.playlistmaker.features.settings.domain.repository.SettingsRepository

class GetThemeSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    fun execute(): Boolean {
        return settingsRepository.getThemeSettings()
    }
}