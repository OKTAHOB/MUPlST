package com.example.playlistmaker.features.media.presentation.viewmodel

import com.example.playlistmaker.features.search.domain.model.Track

sealed class FavoritesState {
    data class Content(val tracks: List<Track>) : FavoritesState()
    object Empty : FavoritesState()
}
