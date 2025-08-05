package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.features.search.data.mapper.TrackMapper
import com.example.playlistmaker.features.search.data.network.RetrofitClient
import com.example.playlistmaker.features.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.features.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.features.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.features.search.domain.repository.TrackRepository
import com.example.playlistmaker.features.search.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.features.search.presentation.viewmodel.SearchViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val searchModule = module {

    single { RetrofitClient.musicApiService }

    single { TrackMapper() }

    single<SharedPreferences>(qualifier = named("search_history_prefs")) {
        androidContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }

    single { Gson() }

    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            sharedPreferences = get(qualifier = named("search_history_prefs")),
            gson = get()
        )
    }

    single {
        SearchTracksUseCase(
            trackRepository = get(),
            searchHistoryRepository = get()
        )
    }

    viewModel {
        SearchViewModel(
            searchTracksUseCase = get()
        )
    }
}
