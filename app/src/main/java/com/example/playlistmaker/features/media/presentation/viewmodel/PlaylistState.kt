package com.example.playlistmaker.features.media.presentation.viewmodel

import com.example.playlistmaker.features.media.domain.model.Playlist

sealed class PlaylistState {
    data class Content(val playlists: List<Playlist>) : PlaylistState()
    object Empty : PlaylistState()
}
