package com.example.playlistmaker.features.search.domain.repository

import com.example.playlistmaker.features.search.domain.model.Track

interface SearchHistoryRepository {
    fun getSearchHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearSearchHistory()
} 