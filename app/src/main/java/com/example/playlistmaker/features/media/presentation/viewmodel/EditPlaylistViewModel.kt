package com.example.playlistmaker.features.media.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.features.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.features.media.domain.model.Playlist
import com.example.playlistmaker.features.media.domain.repository.PlaylistCoverStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditPlaylistViewModel(
    playlistInteractor: PlaylistInteractor,
    private val coverStorage: PlaylistCoverStorage
) : CreatePlaylistViewModel(playlistInteractor) {

    private val _playlistState = MutableStateFlow<Playlist?>(null)
    val playlistState: StateFlow<Playlist?> = _playlistState

    private var currentPlaylist: Playlist? = null
    private var currentPlaylistId: Long? = null

    fun loadPlaylist(playlistId: Long) {
        if (currentPlaylistId == playlistId && currentPlaylist != null) {
            return
        }

        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            currentPlaylistId = playlistId
            if (playlist == null) {
                currentPlaylist = null
                _playlistState.value = null
                _events.emit(CreatePlaylistEvent.PlaylistNotFound)
            } else {
                currentPlaylist = playlist
                _playlistState.value = playlist
            }
        }
    }

    fun savePlaylist(name: String, description: String?, coverUri: String?, coverChanged: Boolean) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return
        }

        val playlist = currentPlaylist ?: return

        viewModelScope.launch {
            try {
                val sanitizedDescription = description?.trim().takeUnless { it.isNullOrEmpty() }
                val finalCoverPath = when {
                    coverChanged && !coverUri.isNullOrBlank() -> {
                        val storedPath = withContext(Dispatchers.IO) {
                            coverStorage.copyCoverToStorage(coverUri, trimmedName)
                        }
                        storedPath.takeUnless { it.isBlank() } ?: playlist.coverPath
                    }
                    coverChanged -> null
                    else -> playlist.coverPath
                }

                val updatedPlaylist = playlist.copy(
                    name = trimmedName,
                    description = sanitizedDescription,
                    coverPath = finalCoverPath
                )

                playlistInteractor.updatePlaylist(updatedPlaylist)
                currentPlaylist = updatedPlaylist
                _playlistState.value = updatedPlaylist
                _events.emit(CreatePlaylistEvent.Success(trimmedName))
            } catch (throwable: Throwable) {
                _events.emit(CreatePlaylistEvent.Error)
            }
        }
    }
}
