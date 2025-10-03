package com.example.playlistmaker.features.player.presentation.viewmodel

import com.example.playlistmaker.features.media.domain.model.Playlist

sealed class PlaylistSelectionState {
    data class Content(val playlists: List<Playlist>) : PlaylistSelectionState()
    object Empty : PlaylistSelectionState()
}
