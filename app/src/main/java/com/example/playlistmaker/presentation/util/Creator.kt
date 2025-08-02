package com.example.playlistmaker.presentation.util

import android.content.Context
import com.example.playlistmaker.SearchHistory
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.preferences.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.usecase.SearchInteractorImpl
import com.example.playlistmaker.domain.usecase.PlayerInteractorImpl
import com.example.playlistmaker.domain.usecase.SettingsInteractorImpl
import com.example.playlistmaker.domain.usecase.interactor.SearchInteractor
import com.example.playlistmaker.domain.usecase.interactor.PlayerInteractor
import com.example.playlistmaker.domain.usecase.interactor.SettingsInteractor

object Creator {
    private val trackMapper = TrackMapper()
    private val trackRepository = TrackRepositoryImpl(trackMapper)

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(
            trackRepository,
            SearchHistory(context.getSharedPreferences("search_history", Context.MODE_PRIVATE))
        )
    }

    fun providePlayerInteractor(): PlayerInteractor = PlayerInteractorImpl()

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(
            SettingsRepositoryImpl(
                context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            )
        )
    }
}