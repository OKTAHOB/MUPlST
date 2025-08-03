package com.example.playlistmaker.features.search.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.domain.usecase.SearchTracksUseCase
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private var currentSearchQuery = ""

    init {
        loadSearchHistory()
    }

    fun searchTracks(query: String) {
        if (query.isEmpty()) {
            loadSearchHistory()
            return
        }

        currentSearchQuery = query
        _searchState.value = SearchState.Loading

        viewModelScope.launch {
            try {
                val tracks = searchTracksUseCase.execute(query)
                if (tracks.isNotEmpty()) {
                    _searchState.value = SearchState.Success(tracks)
                } else {
                    _searchState.value = SearchState.NoResults
                }
            } catch (e: Exception) {
                _searchState.value = SearchState.Error
            }
        }
    }

    fun loadSearchHistory() {
        val history = searchTracksUseCase.getSearchHistory()
        if (history.isNotEmpty()) {
            _searchState.value = SearchState.ShowHistory(history)
        } else {
            _searchState.value = SearchState.Empty
        }
    }

    fun addTrackToHistory(track: Track) {
        searchTracksUseCase.addTrackToHistory(track)
    }

    fun clearSearchHistory() {
        searchTracksUseCase.clearSearchHistory()
        loadSearchHistory()
    }

    fun retrySearch() {
        if (currentSearchQuery.isNotEmpty()) {
            searchTracks(currentSearchQuery)
        }
    }
}

sealed class SearchState {
    object Loading : SearchState()
    data class Success(val tracks: List<Track>) : SearchState()
    object NoResults : SearchState()
    object Error : SearchState()
    data class ShowHistory(val history: List<Track>) : SearchState()
    object Empty : SearchState()
} 