package com.example.playlistmaker.features.media.domain.repository

import com.example.playlistmaker.features.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(trackId: Long)
    fun observeFavorites(): Flow<List<Track>>
}
