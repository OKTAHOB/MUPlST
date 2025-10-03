package com.example.playlistmaker.features.media.di

import com.example.playlistmaker.features.media.data.FavoritesRepositoryImpl
import com.example.playlistmaker.features.media.data.PlaylistCoverStorageImpl
import com.example.playlistmaker.features.media.data.PlaylistRepositoryImpl
import com.example.playlistmaker.features.media.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.features.media.domain.interactor.FavoritesInteractorImpl
import com.example.playlistmaker.features.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.features.media.domain.interactor.PlaylistInteractorImpl
import com.example.playlistmaker.features.media.domain.repository.FavoritesRepository
import com.example.playlistmaker.features.media.domain.repository.PlaylistCoverStorage
import com.example.playlistmaker.features.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.features.media.presentation.viewmodel.CreatePlaylistViewModel
import com.example.playlistmaker.features.media.presentation.viewmodel.FavoritesViewModel
import com.example.playlistmaker.features.media.presentation.viewmodel.MediaLibraryViewModel
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }
    single<PlaylistRepository> { PlaylistRepositoryImpl(get(), get()) }
    single<PlaylistCoverStorage> { PlaylistCoverStorageImpl(androidContext()) }

    single<FavoritesInteractor> { FavoritesInteractorImpl(get()) }
    single<PlaylistInteractor> { PlaylistInteractorImpl(get(), get()) }

    viewModel { MediaLibraryViewModel() }
    viewModel { PlaylistViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { CreatePlaylistViewModel(get()) }
}