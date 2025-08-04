package com.example.playlistmaker.features.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.features.player.domain.repository.PlayerRepository

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.apply {
            setDataSource(url)
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener { onCompletion() }
            prepareAsync()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }
}
