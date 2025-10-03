package com.example.playlistmaker.features.media.domain.interactor

import com.example.playlistmaker.features.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    fun observeFavorites(): Flow<List<Track>>
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(trackId: Long)
}
