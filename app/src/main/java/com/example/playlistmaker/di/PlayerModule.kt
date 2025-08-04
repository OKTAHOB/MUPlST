package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.features.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.features.player.domain.repository.PlayerRepository
import com.example.playlistmaker.features.player.domain.usecase.PlayerInteractor
import com.example.playlistmaker.features.player.presentation.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    single { MediaPlayer() }

    single<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single {
        PlayerInteractor(
            playerRepository = get()
        )
    }

    viewModel {
        PlayerViewModel(
            playerInteractor = get()
        )
    }
}
