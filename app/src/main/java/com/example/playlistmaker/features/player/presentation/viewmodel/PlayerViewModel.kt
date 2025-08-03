package com.example.playlistmaker.features.player.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.player.domain.usecase.PlayerInteractor

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private var isPlaying = false
    private var currentTrack: Track? = null

    fun setTrack(track: Track) {
        currentTrack = track
        _playerState.value = PlayerState(
            track = track,
            playbackState = PlaybackState.TRACK_LOADED,
            currentTime = "00:00"
        )
        preparePlayer()
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
            _playerState.value = _playerState.value?.copy(
                playbackState = PlaybackState.PAUSED
            )
        } else {
            playerInteractor.startPlayer()
            _playerState.value = _playerState.value?.copy(
                playbackState = PlaybackState.PLAYING
            )
        }
        isPlaying = !isPlaying
    }

    fun updateCurrentTime() {
        if (isPlaying) {
            val position = playerInteractor.getCurrentPosition()
            val minutes = position / 60000
            val seconds = (position % 60000) / 1000
            val timeString = String.format("%02d:%02d", minutes, seconds)
            _playerState.value = _playerState.value?.copy(currentTime = timeString)
        }
    }

    fun pausePlayer() {
        if (isPlaying) {
            playerInteractor.pausePlayer()
            _playerState.value = _playerState.value?.copy(
                playbackState = PlaybackState.PAUSED
            )
            isPlaying = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
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
    val currentTime: String = "00:00"
)
