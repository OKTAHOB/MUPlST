package com.example.playlistmaker.features.player.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.player.domain.usecase.PlayerUseCase

class PlayerViewModel(
    private val playerUseCase: PlayerUseCase
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = _currentTime

    private var isPlaying = false
    private var currentTrack: Track? = null

    fun setTrack(track: Track) {
        currentTrack = track
        _playerState.value = PlayerState.TrackLoaded(track)
        preparePlayer()
    }

    private fun preparePlayer() {
        currentTrack?.let { track ->
            playerUseCase.preparePlayer(
                track.previewUrl,
                onPrepared = {
                    _playerState.value = PlayerState.Prepared
                },
                onCompletion = {
                    isPlaying = false
                    _playerState.value = PlayerState.Completed
                    _currentTime.value = "00:00"
                }
            )
        }
    }

    fun togglePlayback() {
        if (isPlaying) {
            playerUseCase.pausePlayer()
            _playerState.value = PlayerState.Paused
        } else {
            playerUseCase.startPlayer()
            _playerState.value = PlayerState.Playing
        }
        isPlaying = !isPlaying
    }

    fun updateCurrentTime() {
        if (isPlaying) {
            val position = playerUseCase.getCurrentPosition()
            val minutes = position / 60000
            val seconds = (position % 60000) / 1000
            _currentTime.value = String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun pausePlayer() {
        if (isPlaying) {
            playerUseCase.pausePlayer()
            _playerState.value = PlayerState.Paused
            isPlaying = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerUseCase.releasePlayer()
    }
}

sealed class PlayerState {
    data class TrackLoaded(val track: Track) : PlayerState()
    object Prepared : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    object Completed : PlayerState()
} 