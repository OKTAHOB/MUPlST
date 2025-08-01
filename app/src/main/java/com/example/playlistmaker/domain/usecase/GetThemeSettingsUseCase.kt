package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.SettingsRepository

class GetThemeSettingsUseCase(
    private val repository: SettingsRepository
) {
    fun execute(): Boolean {
        return repository.getThemeSettings()
    }
}