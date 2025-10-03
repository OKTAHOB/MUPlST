package com.example.playlistmaker.features.player.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.media.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.features.player.domain.usecase.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private var isPlaying = false
    private var currentTrack: Track? = null
    private var progressJob: Job? = null
    private var favoriteJob: Job? = null

    companion object {
        private const val PROGRESS_UPDATE_INTERVAL = 300L
    }

    fun setTrack(track: Track) {
        currentTrack = track
        _playerState.value = PlayerState(
            track = track,
            playbackState = PlaybackState.TRACK_LOADED,
            currentTime = "00:00",
            isFavorite = track.isFavorite
        )
        preparePlayer()
        observeFavoriteState(track.trackId)
    }

    private fun preparePlayer() {
        currentTrack?.let { track ->
            playerInteractor.playTrack(
                track.previewUrl,
                onPrepared = {
                    _playerState.value = _playerState.value?.copy(
                        playbackState = PlaybackState.PREPARED
                    )
                },
                onCompletion = {
                    isPlaying = false
                    stopProgressUpdates()
                    _playerState.value = _playerState.value?.copy(
                        playbackState = PlaybackState.COMPLETED,
                        currentTime = "00:00"
                    )
                }
            )
        }
    }

    fun togglePlayback() {
        if (isPlaying) {
            playerInteractor.pausePlayer()
            isPlaying = false
            _playerState.value = _playerState.value?.copy(
                playbackState = PlaybackState.PAUSED
            )
            stopProgressUpdates()
        } else {
            playerInteractor.startPlayer()
            isPlaying = true
            _playerState.value = _playerState.value?.copy(
                playbackState = PlaybackState.PLAYING
            )
            startProgressUpdates()
        }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive && isPlaying) {
                emitCurrentTime()
                delay(PROGRESS_UPDATE_INTERVAL)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun emitCurrentTime() {
        if (!isPlaying) return
        val position = playerInteractor.getCurrentPosition()
        val minutes = position / 60000
        val seconds = (position % 60000) / 1000
        val timeString = String.format("%02d:%02d", minutes, seconds)
        _playerState.value = _playerState.value?.copy(currentTime = timeString)
    }

    fun pausePlayer() {
        if (isPlaying) {
            playerInteractor.pausePlayer()
            _playerState.value = _playerState.value?.copy(
                playbackState = PlaybackState.PAUSED
            )
            isPlaying = false
            stopProgressUpdates()
        }
    }

    fun onFavoriteClicked() {
        val track = currentTrack ?: return
        val newState = !track.isFavorite
        viewModelScope.launch(Dispatchers.IO) {
            if (newState) {
                favoritesInteractor.addToFavorites(track)
            } else {
                favoritesInteractor.removeFromFavorites(track.trackId)
            }
        }
        updateFavoriteState(newState)
    }

    private fun observeFavoriteState(trackId: Long) {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            favoritesInteractor.observeFavorites()
                .map { tracks -> tracks.any { it.trackId == trackId } }
                .distinctUntilChanged()
                .collect { isFavorite ->
                    updateFavoriteState(isFavorite)
                }
        }
    }

    private fun updateFavoriteState(isFavorite: Boolean) {
        currentTrack = currentTrack?.copy(isFavorite = isFavorite)
        _playerState.postValue(
            _playerState.value?.copy(
                track = currentTrack,
                isFavorite = isFavorite
            ) ?: PlayerState(track = currentTrack, isFavorite = isFavorite)
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressUpdates()
        playerInteractor.releasePlayer()
        favoriteJob?.cancel()
    }
}

enum class PlaybackState {
    TRACK_LOADED,
    PREPARED,
    PLAYING,
    PAUSED,
    COMPLETED
}

data class PlayerState(
    val track: Track? = null,
    val playbackState: PlaybackState = PlaybackState.TRACK_LOADED,
    val currentTime: String = "00:00",
    val isFavorite: Boolean = false
)
