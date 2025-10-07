package com.example.playlistmaker.features.media.presentation.viewmodel

import com.example.playlistmaker.features.media.domain.model.Playlist
import com.example.playlistmaker.features.search.domain.model.Track

sealed class PlaylistDetailsState {
    object Loading : PlaylistDetailsState()
    object NotFound : PlaylistDetailsState()
    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>,
        val totalDurationMillis: Long
    ) : PlaylistDetailsState()
}
