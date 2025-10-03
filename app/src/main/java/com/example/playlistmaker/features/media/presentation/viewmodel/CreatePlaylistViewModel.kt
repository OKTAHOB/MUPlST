package com.example.playlistmaker.features.media.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.features.media.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _events = MutableSharedFlow<CreatePlaylistEvent>(replay = 0, extraBufferCapacity = 1)
    val events: SharedFlow<CreatePlaylistEvent> = _events

    fun createPlaylist(name: String, description: String?, coverUri: String?) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                playlistInteractor.createPlaylist(trimmedName, description, coverUri)
                _events.emit(CreatePlaylistEvent.Success(trimmedName))
            } catch (throwable: Throwable) {
                _events.emit(CreatePlaylistEvent.Error)
            }
        }
    }
}

sealed class CreatePlaylistEvent {
    data class Success(val playlistName: String) : CreatePlaylistEvent()
    object Error : CreatePlaylistEvent()
}
