package com.example.playlistmaker.domain.usecase

import android.media.MediaPlayer
import com.example.playlistmaker.domain.usecase.interactor.PlayerInteractor

class PlayerInteractorImpl : PlayerInteractor {
    private var mediaPlayer: MediaPlayer? = null

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener { onCompletion() }
            prepareAsync()
        }
    }

    override fun startPlayer() {
        mediaPlayer?.start()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
    }

    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    override fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}