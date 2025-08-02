package com.example.playlistmaker.domain.repository

interface SettingsRepository {
    fun getThemeSettings(): Boolean
    fun saveThemeSettings(darkThemeEnabled: Boolean)
}