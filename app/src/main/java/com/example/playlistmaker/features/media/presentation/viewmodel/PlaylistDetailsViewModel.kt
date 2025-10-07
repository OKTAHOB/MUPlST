package com.example.playlistmaker.features.media.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.features.media.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistDetailsState>(PlaylistDetailsState.Loading)
    val state: LiveData<PlaylistDetailsState> = _state

    private val _events = MutableLiveData<PlaylistDetailsEvent>()
    val events: LiveData<PlaylistDetailsEvent> = _events

    private var currentPlaylistId: Long? = null
    private var observeJob: Job? = null
    private var currentContent: PlaylistDetailsState.Content? = null

    fun loadPlaylist(playlistId: Long) {
        if (currentPlaylistId == playlistId) return
        currentPlaylistId = playlistId
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _state.postValue(PlaylistDetailsState.Loading)
            playlistInteractor.observePlaylist(playlistId).collectLatest { playlist ->
                if (playlist == null) {
                    currentContent = null
                    _state.postValue(PlaylistDetailsState.NotFound)
                } else {
                    val tracks = playlistInteractor.getTracksByIds(playlist.trackIds.asReversed())
                    val totalDurationMillis = tracks.sumOf { it.trackTime }
                    val contentState = PlaylistDetailsState.Content(
                        playlist = playlist,
                        tracks = tracks,
                        totalDurationMillis = totalDurationMillis
                    )
                    currentContent = contentState
                    _state.postValue(contentState)
                }
            }
        }
    }

    fun removeTrack(trackId: Long) {
        val playlistId = currentPlaylistId ?: return
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(playlistId, trackId)
        }
    }

    fun deletePlaylist() {
        val playlistId = currentPlaylistId ?: return
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistId)
            _events.postValue(PlaylistDetailsEvent.PlaylistDeleted)
        }
    }

    fun getCurrentContent(): PlaylistDetailsState.Content? = currentContent
}

sealed class PlaylistDetailsEvent {
    object PlaylistDeleted : PlaylistDetailsEvent()
}
