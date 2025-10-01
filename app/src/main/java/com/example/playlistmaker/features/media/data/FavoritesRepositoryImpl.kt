package com.example.playlistmaker.features.media.data

import com.example.playlistmaker.data.db.FavoritesDao
import com.example.playlistmaker.features.media.domain.repository.FavoritesRepository
import com.example.playlistmaker.features.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val favoritesDao: FavoritesDao
) : FavoritesRepository {

    override suspend fun addToFavorites(track: Track) {
        favoritesDao.insertTrack(FavoriteTrackMapper.mapToEntity(track))
    }

    override suspend fun removeFromFavorites(trackId: Long) {
        favoritesDao.deleteTrack(trackId)
    }

    override fun observeFavorites(): Flow<List<Track>> =
        favoritesDao.observeFavoriteTracks().map { entities ->
            entities.map { entity -> FavoriteTrackMapper.mapToDomain(entity) }
        }
}
