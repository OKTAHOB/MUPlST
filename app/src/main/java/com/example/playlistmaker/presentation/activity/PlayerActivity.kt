package com.example.playlistmaker.presentation.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var track: Track
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var handler: Handler
    private lateinit var playButton: ImageView
    private lateinit var currentTimeTextView: TextView
    private var updateTimeRunnable: Runnable? = null

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val trackJson = intent.getStringExtra("TRACK_JSON") ?: ""
        track = Gson().fromJson(trackJson, Track::class.java)

        mediaPlayer = MediaPlayer()
        handler = Handler(Looper.getMainLooper())

        initViews()
        setupBackButton()
        preparePlayer()
    }

    private fun initViews() {
        playButton = findViewById(R.id.player_btn_play)
        currentTimeTextView = findViewById(R.id.player_current_time)

        Glide.with(this)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .into(findViewById(R.id.player_art))

        findViewById<TextView>(R.id.player_track_name).text = track.trackName
        findViewById<TextView>(R.id.player_track_artist).text = track.artistName

        track.collectionName?.takeIf { it.isNotEmpty() }?.let {
            findViewById<TextView>(R.id.player_track_album_value).text = it
        } ?: run {
            findViewById<TextView>(R.id.player_track_album_title).visibility = View.GONE
            findViewById<TextView>(R.id.player_track_album_value).visibility = View.GONE
        }

        val year = track.releaseDate.split("-").firstOrNull() ?: track.releaseDate
        findViewById<TextView>(R.id.player_track_year_value).text = year

        findViewById<TextView>(R.id.player_track_genre_value).text = track.primaryGenreName
        findViewById<TextView>(R.id.player_track_country_value).text = track.country

        val minutes = track.trackTime / 60000
        val seconds = (track.trackTime % 60000) / 1000
        findViewById<TextView>(R.id.player_track_time_value).text =
            String.format("%02d:%02d", minutes, seconds)
    }

    private fun preparePlayer() {
        try {
            mediaPlayer.setDataSource(track.previewUrl)
            mediaPlayer.prepareAsync()

            mediaPlayer.setOnPreparedListener {
                playerState = STATE_PREPARED
                playButton.isEnabled = true
                playButton.setImageResource(R.drawable.btn_play)
            }

            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                playButton.setImageResource(R.drawable.btn_play)
                stopTimeUpdater()
                currentTimeTextView.text = "00:00"
            }

            playButton.setOnClickListener {
                playbackControl()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PREPARED -> {
                mediaPlayer.seekTo(0)
                startPlayback()
            }
            STATE_PAUSED -> startPlayback()
            STATE_PLAYING -> pausePlayback()
        }
    }

    private fun startPlayback() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playButton.setImageResource(R.drawable.btn_pause)
        startTimeUpdater()
    }

    private fun pausePlayback() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playButton.setImageResource(R.drawable.btn_play)
        stopTimeUpdater()
    }

    private fun stopPlayback() {
        mediaPlayer.stop()
        playerState = STATE_PREPARED
        mediaPlayer.prepareAsync()
        playButton.setImageResource(R.drawable.btn_play)
        stopTimeUpdater()
        currentTimeTextView.text = "00:00"
    }

    private fun startTimeUpdater() {
        updateTimeRunnable = object : Runnable {
            override fun run() {
                val currentPosition = mediaPlayer.currentPosition
                val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                currentTimeTextView.text = formattedTime
                handler.postDelayed(this, 300)
            }
        }
        handler.post(updateTimeRunnable as Runnable)
    }

    private fun stopTimeUpdater() {
        updateTimeRunnable?.let {
            handler.removeCallbacks(it)
        }
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimeUpdater()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.player_back).setOnClickListener {
            if (playerState == STATE_PLAYING || playerState == STATE_PAUSED) {
                stopPlayback()
            }
            finish()
        }
    }
}