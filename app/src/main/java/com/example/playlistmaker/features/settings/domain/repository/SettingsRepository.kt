package com.example.playlistmaker.features.settings.domain.repository

interface SettingsRepository {
    fun getThemeSettings(): Boolean
    fun saveThemeSettings(darkThemeEnabled: Boolean)
    fun applyTheme(darkThemeEnabled: Boolean)
} 