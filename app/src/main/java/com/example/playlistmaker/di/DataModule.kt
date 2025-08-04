package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.features.search.data.mapper.TrackMapper
import com.example.playlistmaker.features.search.data.network.RetrofitClient
import com.example.playlistmaker.features.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.features.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.features.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.features.search.domain.repository.TrackRepository
import com.example.playlistmaker.features.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.features.player.domain.repository.PlayerRepository
import com.example.playlistmaker.features.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.features.settings.domain.repository.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {

    // Network
    single { RetrofitClient.musicApiService }

    // Mappers
    single { TrackMapper() }

    // SharedPreferences
    single<SharedPreferences>(qualifier = named("search_history_prefs")) {
        androidContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }

    single<SharedPreferences>(qualifier = named("settings_prefs")) {
        androidContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    // Repositories
    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(qualifier = named("search_history_prefs")))
    }

    single<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(qualifier = named("settings_prefs")))
    }
}
