package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackRepository

class SearchTracksUseCase(private val repository: TrackRepository) {
    suspend fun execute(expression: String): List<Track> {
        return repository.searchTracks(expression)
    }
}