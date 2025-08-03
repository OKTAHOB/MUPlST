package com.example.playlistmaker.features.settings.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.features.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.features.settings.domain.usecase.SettingsUseCase

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            val settingsRepository = SettingsRepositoryImpl(
                context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            )
            val settingsUseCase = SettingsUseCase(settingsRepository)
            
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 