package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val trackJson = intent.getStringExtra("TRACK_JSON") ?: ""
        val track = Gson().fromJson(trackJson, Track::class.java)

        setupViews(track)
        setupBackButton()
    }

    private fun setupViews(track: Track) {
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .into(findViewById<ImageView>(R.id.player_art))

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

        val minutes = track.trackTime?.div(60000)
        val seconds = (track.trackTime?.rem(60000))?.div(1000)
        findViewById<TextView>(R.id.player_track_time_value).text =
            String.format("%02d:%02d", minutes, seconds)
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.player_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}