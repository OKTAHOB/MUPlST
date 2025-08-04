package com.example.playlistmaker.di

import com.example.playlistmaker.features.search.presentation.viewmodel.SearchViewModel
import com.example.playlistmaker.features.player.presentation.viewmodel.PlayerViewModel
import com.example.playlistmaker.features.settings.presentation.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    viewModel {
        SearchViewModel(
            searchTracksUseCase = get()
        )
    }

    viewModel {
        PlayerViewModel(
            playerInteractor = get()
        )
    }

    viewModel {
        SettingsViewModel(
            settingsUseCase = get()
        )
    }
}
