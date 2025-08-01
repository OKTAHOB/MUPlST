package com.example.playlistmaker.presentation.util

import android.content.Context
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.preferences.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.usecase.GetThemeSettingsUseCase
import com.example.playlistmaker.domain.usecase.SaveThemeSettingsUseCase
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase

object Creator {
    private val trackMapper = TrackMapper()
    private val trackRepository = TrackRepositoryImpl(trackMapper)

    private fun provideSettingsRepository(context: Context): SettingsRepositoryImpl {
        return SettingsRepositoryImpl(
            context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        )
    }

    fun provideSearchTracksUseCase() = SearchTracksUseCase(trackRepository)

    fun provideGetThemeSettingsUseCase(context: Context) =
        GetThemeSettingsUseCase(provideSettingsRepository(context))

    fun provideSaveThemeSettingsUseCase(context: Context) =
        SaveThemeSettingsUseCase(provideSettingsRepository(context))
}