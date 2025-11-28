package com.example.playlistmaker.features.player.service

sealed interface PlayerServiceState {
    object Idle : PlayerServiceState
    object Prepared : PlayerServiceState
    data class Playing(val currentPosition: Int) : PlayerServiceState
    data class Paused(val currentPosition: Int) : PlayerServiceState
    data class Completed(val currentPosition: Int) : PlayerServiceState
}
