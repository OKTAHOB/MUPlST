package com.example.playlistmaker.presentation.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.usecase.interactor.PlayerInteractor
import com.example.playlistmaker.presentation.util.Creator
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var playerInteractor: PlayerInteractor
    private lateinit var track: Track
    private lateinit var playButton: ImageView
    private lateinit var currentTimeTextView: TextView
    private var isPlaying = false

    companion object {
        private const val UPDATE_TIME_DELAY = 300L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerInteractor = Creator.providePlayerInteractor()
        track = Gson().fromJson(intent.getStringExtra("TRACK_JSON"), Track::class.java)

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
        playerInteractor.preparePlayer(
            track.previewUrl,
            onPrepared = {
                playButton.isEnabled = true
                playButton.setImageResource(R.drawable.btn_play)
            },
            onCompletion = {
                isPlaying = false
                playButton.setImageResource(R.drawable.btn_play)
                currentTimeTextView.text = "00:00"
            }
        )

        playButton.setOnClickListener {
            playbackControl()
        }
    }

    private fun playbackControl() {
        if (isPlaying) {
            playerInteractor.pausePlayer()
            playButton.setImageResource(R.drawable.btn_play)
        } else {
            playerInteractor.startPlayer()
            playButton.setImageResource(R.drawable.btn_pause)
            startTimeUpdater()
        }
        isPlaying = !isPlaying
    }

    private fun startTimeUpdater() {
        currentTimeTextView.postDelayed(updateTimeRunnable, UPDATE_TIME_DELAY)
    }

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (isPlaying) {
                val currentPosition = playerInteractor.getCurrentPosition()
                val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                currentTimeTextView.text = formattedTime
                currentTimeTextView.postDelayed(this, UPDATE_TIME_DELAY)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            playerInteractor.pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.releasePlayer()
        currentTimeTextView.removeCallbacks(updateTimeRunnable)
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.player_back).setOnClickListener {
            finish()
        }
    }
}