package com.example.playlistmaker.features.search.domain.repository

import com.example.playlistmaker.features.search.domain.model.Track

interface TrackRepository {
    suspend fun searchTracks(expression: String): List<Track>
} 