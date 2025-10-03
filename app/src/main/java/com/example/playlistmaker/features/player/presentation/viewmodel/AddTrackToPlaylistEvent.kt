package com.example.playlistmaker.features.player.presentation.viewmodel

sealed class AddTrackToPlaylistEvent {
    data class Added(val playlistName: String) : AddTrackToPlaylistEvent()
    data class AlreadyExists(val playlistName: String) : AddTrackToPlaylistEvent()
}
