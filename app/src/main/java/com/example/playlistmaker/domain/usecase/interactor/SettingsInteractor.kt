package com.example.playlistmaker.domain.usecase.interactor

interface SettingsInteractor {
    fun getThemeSettings(): Boolean
    fun saveThemeSettings(darkThemeEnabled: Boolean)
    fun applyTheme(darkThemeEnabled: Boolean)
}