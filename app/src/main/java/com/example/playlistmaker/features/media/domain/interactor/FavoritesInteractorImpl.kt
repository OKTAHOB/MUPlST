package com.example.playlistmaker.features.media.domain.interactor

import com.example.playlistmaker.features.media.domain.repository.FavoritesRepository
import com.example.playlistmaker.features.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {

    override fun observeFavorites(): Flow<List<Track>> = favoritesRepository.observeFavorites()

    override suspend fun addToFavorites(track: Track) {
        favoritesRepository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(trackId: Long) {
        favoritesRepository.removeFromFavorites(trackId)
    }
}
