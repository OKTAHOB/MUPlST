package com.example.playlistmaker.features.player.domain.repository

interface PlayerRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun getCurrentPosition(): Int
    fun releasePlayer()
} 