package com.example.playlistmaker.features.search.data.repository

import com.example.playlistmaker.features.search.data.dto.SearchResponse
import com.example.playlistmaker.features.search.data.mapper.TrackMapper
import com.example.playlistmaker.features.search.data.network.RetrofitClient
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.domain.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepositoryImpl(private val trackMapper: TrackMapper) : TrackRepository {
    override suspend fun searchTracks(expression: String): List<Track> = withContext(Dispatchers.IO) {
        try {
            val response: SearchResponse = RetrofitClient.musicApiService.search(expression)
            response.results.map { trackDto ->
                trackMapper.map(trackDto)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
} 