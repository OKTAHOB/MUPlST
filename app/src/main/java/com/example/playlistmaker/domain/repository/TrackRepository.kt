package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface TrackRepository {
    suspend fun searchTracks(expression: String): List<Track>
}