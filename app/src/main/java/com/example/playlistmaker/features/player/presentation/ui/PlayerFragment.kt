package com.example.playlistmaker.features.player.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.features.player.presentation.viewmodel.PlaybackState
import com.example.playlistmaker.features.player.presentation.viewmodel.PlayerViewModel
import com.example.playlistmaker.features.search.domain.model.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private lateinit var playButton: ImageView
    private lateinit var currentTimeTextView: TextView
    private lateinit var track: Track
    private lateinit var likeButtonContainer: ImageView
    private lateinit var likeButtonIcon: ImageView

    private val viewModel: PlayerViewModel by viewModel()
    companion object {
        private const val TRACK_JSON_KEY = "track_json"

        fun newInstance(trackJson: String): PlayerFragment {
            return PlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(TRACK_JSON_KEY, trackJson)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trackJson = arguments?.getString(TRACK_JSON_KEY)
            ?: arguments?.getString("trackJson")
            ?: ""

        if (trackJson.isNotEmpty()) {
            track = Gson().fromJson(trackJson, Track::class.java)
            initViews()
            setupBackButton()
            observeViewModel()
            viewModel.setTrack(track)
        }
    }

    private fun initViews() {
        playButton = requireView().findViewById(R.id.player_btn_play)
        currentTimeTextView = requireView().findViewById(R.id.player_current_time)
        likeButtonContainer = requireView().findViewById(R.id.player_btn_like)
        likeButtonIcon = requireView().findViewById(R.id.player_btn_like_ico)

        val artworkUrl = track.artworkUrl512.ifEmpty {
            track.artworkUrl100.replace("100x100", "512x512")
        }
        Glide.with(this)
            .load(artworkUrl)
            .placeholder(R.drawable.placeholder)
            .transform(com.bumptech.glide.load.resource.bitmap.RoundedCorners(8))
            .into(requireView().findViewById(R.id.player_art))

        requireView().findViewById<TextView>(R.id.player_track_name).text = track.trackName
        requireView().findViewById<TextView>(R.id.player_track_artist).text = track.artistName

        track.collectionName?.takeIf { it.isNotEmpty() }?.let {
            requireView().findViewById<TextView>(R.id.player_track_album_value).text = it
        } ?: run {
            requireView().findViewById<TextView>(R.id.player_track_album_title).visibility = View.GONE
            requireView().findViewById<TextView>(R.id.player_track_album_value).visibility = View.GONE
        }

        val year = track.releaseDate.split("-").firstOrNull() ?: track.releaseDate
        requireView().findViewById<TextView>(R.id.player_track_year_value).text = year

        requireView().findViewById<TextView>(R.id.player_track_genre_value).text = track.primaryGenreName
        requireView().findViewById<TextView>(R.id.player_track_country_value).text = track.country

        val minutes = track.trackTime / 60000
        val seconds = (track.trackTime % 60000) / 1000
        requireView().findViewById<TextView>(R.id.player_track_time_value).text =
            String.format("%02d:%02d", minutes, seconds)

        playButton.setOnClickListener {
            viewModel.togglePlayback()
        }

        val favoriteClickListener = View.OnClickListener {
            viewModel.onFavoriteClicked()
        }
        likeButtonContainer.setOnClickListener(favoriteClickListener)
        likeButtonIcon.setOnClickListener(favoriteClickListener)
    }

    private fun observeViewModel() {
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
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
                }

                PlaybackState.PAUSED -> {
                    playButton.setImageResource(R.drawable.btn_play)
                }

                PlaybackState.COMPLETED -> {
                    playButton.setImageResource(R.drawable.btn_play)
                }
            }

            currentTimeTextView.text = state.currentTime
            likeButtonIcon.setImageResource(
                if (state.isFavorite) R.drawable.btn_like_filled else R.drawable.btn_like
            )
        }
    }

    private fun setupBackButton() {
        requireView().findViewById<ImageView>(R.id.player_back).setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }
} 