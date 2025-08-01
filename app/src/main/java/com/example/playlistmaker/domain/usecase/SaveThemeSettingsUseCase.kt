package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.SettingsRepository

class SaveThemeSettingsUseCase(
    private val repository: SettingsRepository
) {
    fun execute(darkThemeEnabled: Boolean) {
        repository.saveThemeSettings(darkThemeEnabled)
    }
}