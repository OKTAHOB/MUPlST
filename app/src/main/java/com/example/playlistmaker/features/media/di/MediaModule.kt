package com.example.playlistmaker.features.media.di

import com.example.playlistmaker.features.media.presentation.viewmodel.MediaLibraryViewModel
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistViewModel
import com.example.playlistmaker.features.media.presentation.viewmodel.FavoritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    viewModel { MediaLibraryViewModel() }
    viewModel { PlaylistViewModel() }
    viewModel { FavoritesViewModel() }
}