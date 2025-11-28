package com.example.playlistmaker.features.player.service

import kotlinx.coroutines.flow.StateFlow

interface PlayerServiceController {
    val playerState: StateFlow<PlayerServiceState>

    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun getCurrentPosition(): Int
    fun releasePlayer()
    fun showForegroundNotification()
    fun hideForegroundNotification()
}
