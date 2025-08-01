package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.SearchResponse
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackRepository
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