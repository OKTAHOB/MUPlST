package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.SearchHistory
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.usecase.interactor.SearchInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchInteractorImpl(
    private val trackRepository: TrackRepository,
    private val searchHistory: SearchHistory
) : SearchInteractor {

    override suspend fun searchTracks(expression: String): List<Track> =
        withContext(Dispatchers.IO) {
            trackRepository.searchTracks(expression)
        }

    override fun getSearchHistory(): List<Track> = searchHistory.getHistory()

    override fun addTrackToHistory(track: Track) = searchHistory.addTrack(track)

    override fun clearSearchHistory() = searchHistory.clear()
}