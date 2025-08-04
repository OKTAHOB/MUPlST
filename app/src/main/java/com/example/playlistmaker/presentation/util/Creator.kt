package com.example.playlistmaker.presentation.util

import android.content.Context
import com.example.playlistmaker.features.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.features.player.domain.usecase.PlayerInteractor
import com.example.playlistmaker.features.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.features.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.features.search.data.mapper.TrackMapper
import com.example.playlistmaker.features.search.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.features.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.features.settings.domain.usecase.GetThemeSettingsUseCase
import com.example.playlistmaker.features.settings.domain.usecase.SaveThemeSettingsUseCase

object Creator {
    private val trackMapper = TrackMapper()
    private val trackRepository = TrackRepositoryImpl(trackMapper)

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractor(PlayerRepositoryImpl())
    }

    fun provideSearchTracksUseCase(context: Context): SearchTracksUseCase {
        val searchHistoryRepository = SearchHistoryRepositoryImpl(
            context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
        )
        return SearchTracksUseCase(trackRepository, searchHistoryRepository)
    }

    fun provideSettingsRepository(context: Context): SettingsRepositoryImpl {
        return SettingsRepositoryImpl(
            context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        )
    }

    fun provideGetThemeSettingsUseCase(context: Context): GetThemeSettingsUseCase {
        return GetThemeSettingsUseCase(provideSettingsRepository(context))
    }

    fun provideSaveThemeSettingsUseCase(context: Context): SaveThemeSettingsUseCase {
        return SaveThemeSettingsUseCase(provideSettingsRepository(context))
    }
}