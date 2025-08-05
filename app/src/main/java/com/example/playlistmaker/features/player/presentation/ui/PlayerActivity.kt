package com.example.playlistmaker.features.player.presentation.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.features.player.presentation.viewmodel.PlaybackState
import com.example.playlistmaker.features.player.presentation.viewmodel.PlayerViewModel
import com.example.playlistmaker.features.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageView
    private lateinit var currentTimeTextView: TextView
    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel ()
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            viewModel.updateCurrentTime()
            handler.postDelayed(this, 300L)
        }
    }

    companion object {
        private const val UPDATE_TIME_DELAY = 300L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        track = Gson().fromJson(intent.getStringExtra("TRACK_JSON"), Track::class.java)

        initViews()
        setupBackButton()
        observeViewModel()
        viewModel.setTrack(track)
    }

    private fun initViews() {
        playButton = findViewById(R.id.player_btn_play)
        currentTimeTextView = findViewById(R.id.player_current_time)

        val artworkUrl = track.artworkUrl100.replace("100x100", "512x512")
        Glide.with(this)
            .load(artworkUrl)
            .placeholder(R.drawable.placeholder)
            .transform(com.bumptech.glide.load.resource.bitmap.RoundedCorners(8))
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

        playButton.setOnClickListener {
            viewModel.togglePlayback()
        }
    }

    private fun observeViewModel() {
        viewModel.playerState.observe(this) { state ->
            when (state.playbackState) {
                PlaybackState.TRACK_LOADED -> {
                    // Track is loaded, UI is already set up
                }

                PlaybackState.PREPARED -> {
                    playButton.isEnabled = true
                    playButton.setImageResource(R.drawable.btn_play)
                }

                PlaybackState.PLAYING -> {
                    playButton.setImageResource(R.drawable.btn_pause)
                    startTimeUpdater()
                }

                PlaybackState.PAUSED -> {
                    playButton.setImageResource(R.drawable.btn_play)
                    stopTimeUpdater()
                }

                PlaybackState.COMPLETED -> {
                    playButton.setImageResource(R.drawable.btn_play)
                    stopTimeUpdater()
                }
            }


            currentTimeTextView.text = state.currentTime
        }
    }

    private fun startTimeUpdater() {
        handler.postDelayed(updateTimeRunnable, UPDATE_TIME_DELAY)
    }

    private fun stopTimeUpdater() {
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.player_back).setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimeUpdater()
    }
} 