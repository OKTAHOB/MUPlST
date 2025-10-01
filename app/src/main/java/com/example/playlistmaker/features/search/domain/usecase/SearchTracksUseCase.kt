package com.example.playlistmaker.features.search.domain.usecase

import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.features.search.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow

class SearchTracksUseCase(
    private val trackRepository: TrackRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) {
    fun execute(expression: String): Flow<List<Track>> {
        return trackRepository.searchTracks(expression)
    }

    suspend fun getSearchHistory(): List<Track> {
        return searchHistoryRepository.getSearchHistory()
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryRepository.addTrackToHistory(track)
    }

    fun clearSearchHistory() {
        searchHistoryRepository.clearSearchHistory()
    }
} 