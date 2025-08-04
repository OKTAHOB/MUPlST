package com.example.playlistmaker.features.player.domain.usecase

import com.example.playlistmaker.features.player.domain.repository.PlayerRepository

class PlayerUseCase(
    private val playerRepository: PlayerRepository
) {
    fun playTrack(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        playerRepository.preparePlayer(url, onPrepared, onCompletion)
    }
} 