package com.example.playlistmaker.features.player.domain.usecase

import com.example.playlistmaker.features.player.domain.repository.PlayerRepository

class PlayerUseCase(
    private val playerRepository: PlayerRepository
) {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        playerRepository.preparePlayer(url, onPrepared, onCompletion)
    }

    fun startPlayer() {
        playerRepository.startPlayer()
    }

    fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }

    fun releasePlayer() {
        playerRepository.releasePlayer()
    }
} 