package com.example.playlistmaker.features.media.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.features.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.features.media.domain.model.Playlist
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistState>(PlaylistState.Empty)
    val state: LiveData<PlaylistState> = _state

    init {
        observePlaylists()
    }

    fun refreshPlaylists() {
        viewModelScope.launch {
            val playlists = playlistInteractor.getPlaylists()
            emitState(playlists)
        }
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            playlistInteractor.observePlaylists().collectLatest { playlists ->
                emitState(playlists)
            }
        }
    }

    private fun emitState(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            _state.postValue(PlaylistState.Empty)
        } else {
            _state.postValue(PlaylistState.Content(playlists))
        }
    }
}