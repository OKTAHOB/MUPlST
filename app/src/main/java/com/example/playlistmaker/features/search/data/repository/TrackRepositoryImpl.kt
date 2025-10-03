package com.example.playlistmaker.features.search.data.repository

import com.example.playlistmaker.data.db.FavoritesDao
import com.example.playlistmaker.features.search.data.mapper.TrackMapper
import com.example.playlistmaker.features.search.data.network.ApiService
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.domain.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TrackRepositoryImpl(
    private val apiService: ApiService,
    private val trackMapper: TrackMapper,
    private val favoritesDao: FavoritesDao
) : TrackRepository {
    override fun searchTracks(expression: String): Flow<List<Track>> =
        flow {
            val response = apiService.search(expression)
            val tracks = response.results.map { trackDto -> trackMapper.map(trackDto) }
            val favoriteIds = favoritesDao.getFavoriteTrackIds().toSet()
            tracks.forEach { track ->
                track.isFavorite = favoriteIds.contains(track.trackId)
            }
            emit(tracks)
        }.flowOn(Dispatchers.IO)
}