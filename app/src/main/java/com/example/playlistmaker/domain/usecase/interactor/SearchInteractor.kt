package com.example.playlistmaker.domain.usecase.interactor

import com.example.playlistmaker.domain.model.Track

interface SearchInteractor {
    suspend fun searchTracks(expression: String): List<Track>
    fun getSearchHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearSearchHistory()
}