package com.example.playlistmaker.features.player.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerService : Service(), PlayerServiceController {

    private val mediaPlayer = MediaPlayer()
    private val binder = PlayerBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    private val _playerState = MutableStateFlow<PlayerServiceState>(PlayerServiceState.Idle)
    override val playerState: StateFlow<PlayerServiceState> = _playerState

    private var trackUrl: String = ""
    private var trackName: String = ""
    private var artistName: String = ""

    companion object {
        private const val TAG = "PlayerService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "player_channel"
        private const val PROGRESS_UPDATE_INTERVAL = 300L
        
        const val EXTRA_TRACK_URL = "track_url"
        const val EXTRA_TRACK_NAME = "track_name"
        const val EXTRA_ARTIST_NAME = "artist_name"
    }

    inner class PlayerBinder : Binder() {
        fun getService(): PlayerServiceController = this@PlayerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        trackUrl = intent?.getStringExtra(EXTRA_TRACK_URL) ?: ""
        trackName = intent?.getStringExtra(EXTRA_TRACK_NAME) ?: ""
        artistName = intent?.getStringExtra(EXTRA_ARTIST_NAME) ?: ""
        return binder
    }

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        Log.d(TAG, "preparePlayer called with url: $url")
        trackUrl = url
        try {
            mediaPlayer.apply {
                reset()
                setDataSource(url)
                setOnPreparedListener {
                    Log.d(TAG, "MediaPlayer prepared successfully")
                    onPrepared()
                    _playerState.value = PlayerServiceState.Prepared
                }
                setOnCompletionListener {
                    Log.d(TAG, "MediaPlayer playback completed")
                    onCompletion()
                    _playerState.value = PlayerServiceState.Completed(0)
                    stopProgressUpdates()
                }
                setOnErrorListener { mp, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    false
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error preparing player: ${e.message}", e)
        }
    }

    override fun startPlayer() {
        Log.d(TAG, "startPlayer called")
        try {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                Log.d(TAG, "MediaPlayer started successfully")
                _playerState.value = PlayerServiceState.Playing(mediaPlayer.currentPosition)
                startProgressUpdates()
            } else {
                Log.d(TAG, "MediaPlayer is already playing")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting player: ${e.message}", e)
        }
    }

    override fun pausePlayer() {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                _playerState.value = PlayerServiceState.Paused(mediaPlayer.currentPosition)
                stopProgressUpdates()
            }
        } catch (e: Exception) {
            // MediaPlayer not ready
        }
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun releasePlayer() {
        stopProgressUpdates()
        mediaPlayer.reset()
        mediaPlayer.setOnPreparedListener(null)
        mediaPlayer.setOnCompletionListener(null)
        _playerState.value = PlayerServiceState.Idle
    }

    override fun showForegroundNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("$artistName - $trackName")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            } else {
                0
            }
        )
    }

    override fun hideForegroundNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = serviceScope.launch {
            while (isActive) {
                if (mediaPlayer.isPlaying) {
                    _playerState.value = PlayerServiceState.Playing(mediaPlayer.currentPosition)
                }
                delay(PROGRESS_UPDATE_INTERVAL)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Player Notifications",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for audio playback"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        mediaPlayer.release()
    }
}
