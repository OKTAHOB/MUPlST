package com.example.playlistmaker.features.search.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.domain.usecase.SearchTracksUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private var currentSearchQuery = ""
    private var searchJob: Job? = null

    init {
        loadSearchHistory()
    }

    fun searchTracks(query: String) {
        if (query.isEmpty()) {
            searchJob?.cancel()
            loadSearchHistory()
            return
        }

        currentSearchQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchTracksUseCase.execute(query)
                .onStart { _searchState.value = SearchState.Loading }
                .catch { _searchState.value = SearchState.Error }
                .collect { tracks ->
                    if (tracks.isNotEmpty()) {
                        _searchState.value = SearchState.Success(tracks)
                    } else {
                        _searchState.value = SearchState.NoResults
                    }
                }
        }
    }

    fun loadSearchHistory() {
        viewModelScope.launch {
            val history = searchTracksUseCase.getSearchHistory()
            if (history.isNotEmpty()) {
                _searchState.postValue(SearchState.ShowHistory(history))
            } else {
                _searchState.postValue(SearchState.Empty)
            }
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

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
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