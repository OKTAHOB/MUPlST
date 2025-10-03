package com.example.playlistmaker.features.search.domain.repository

import com.example.playlistmaker.features.search.domain.model.Track

interface SearchHistoryRepository {
    suspend fun getSearchHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearSearchHistory()
} 