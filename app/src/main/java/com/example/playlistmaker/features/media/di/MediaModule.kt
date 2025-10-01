package com.example.playlistmaker.features.media.di

import com.example.playlistmaker.features.media.data.FavoritesRepositoryImpl
import com.example.playlistmaker.features.media.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.features.media.domain.interactor.FavoritesInteractorImpl
import com.example.playlistmaker.features.media.domain.repository.FavoritesRepository
import com.example.playlistmaker.features.media.presentation.viewmodel.FavoritesViewModel
import com.example.playlistmaker.features.media.presentation.viewmodel.MediaLibraryViewModel
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }

    single<FavoritesInteractor> { FavoritesInteractorImpl(get()) }

    viewModel { MediaLibraryViewModel() }
    viewModel { PlaylistViewModel() }
    viewModel { FavoritesViewModel(get()) }
}