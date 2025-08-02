package com.example.playlistmaker.features.settings.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.features.settings.domain.usecase.SettingsUseCase

class SettingsViewModel(
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {

    private val _themeSettings = MutableLiveData<Boolean>()
    val themeSettings: LiveData<Boolean> = _themeSettings

    init {
        loadThemeSettings()
    }

    fun loadThemeSettings() {
        val isDarkTheme = settingsUseCase.getThemeSettings()
        _themeSettings.value = isDarkTheme
    }

    fun updateThemeSettings(isDarkTheme: Boolean) {
        settingsUseCase.saveThemeSettings(isDarkTheme)
        settingsUseCase.applyTheme(isDarkTheme)
        _themeSettings.value = isDarkTheme
    }
} 