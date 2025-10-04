package com.example.playlistmaker.features.player.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.features.player.presentation.viewmodel.PlaybackState
import com.example.playlistmaker.features.player.presentation.viewmodel.AddTrackToPlaylistEvent
import com.example.playlistmaker.features.player.presentation.viewmodel.PlaylistSelectionState
import com.example.playlistmaker.features.player.presentation.viewmodel.PlayerViewModel
import com.example.playlistmaker.features.search.domain.model.Track
import com.google.gson.Gson
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private lateinit var playButton: ImageView
    private lateinit var currentTimeTextView: TextView
    private lateinit var track: Track
    private lateinit var likeButtonContainer: ImageView
    private lateinit var likeButtonIcon: ImageView
    private lateinit var addToPlaylistContainer: ImageView
    private lateinit var addToPlaylistIcon: ImageView
    private lateinit var playlistsAdapter: PlayerPlaylistsAdapter
    private lateinit var playlistsRecyclerView: RecyclerView
    private lateinit var playlistsEmptyView: TextView
    private lateinit var newPlaylistButton: MaterialButton
    private lateinit var overlayView: View
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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
            setupBottomSheet()
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
        addToPlaylistContainer = requireView().findViewById(R.id.player_btn_add_to_list)
        addToPlaylistIcon = requireView().findViewById(R.id.player_btn_add_to_list_ico)

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

        val addToPlaylistClickListener = View.OnClickListener {
            showBottomSheet()
        }
        addToPlaylistContainer.setOnClickListener(addToPlaylistClickListener)
        addToPlaylistIcon.setOnClickListener(addToPlaylistClickListener)
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

        viewModel.playlistsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistSelectionState.Content -> {
                    playlistsAdapter.submitList(state.playlists)
                    playlistsRecyclerView.visibility = View.VISIBLE
                    playlistsEmptyView.visibility = View.GONE
                }
                PlaylistSelectionState.Empty -> {
                    playlistsAdapter.submitList(emptyList())
                    playlistsRecyclerView.visibility = View.GONE
                    playlistsEmptyView.visibility = View.VISIBLE
                }
            }
        }

        viewModel.addTrackEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is AddTrackToPlaylistEvent.Added -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.track_added_to_playlist, event.playlistName),
                        Toast.LENGTH_SHORT
                    ).show()
                    hideBottomSheet()
                }
                is AddTrackToPlaylistEvent.AlreadyExists -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.track_already_in_playlist, event.playlistName),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                null -> {}
            }
            if (event != null) {
                viewModel.onAddTrackEventConsumed()
            }
        }
    }

    private fun setupBackButton() {
        requireView().findViewById<ImageView>(R.id.player_back).setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupBottomSheet() {
        overlayView = requireView().findViewById(R.id.playerOverlay)
        val bottomSheetContainer = requireView().findViewById<LinearLayout>(R.id.playerPlaylistsBottomSheet)
        playlistsRecyclerView = requireView().findViewById(R.id.playerPlaylistsRecyclerView)
        playlistsEmptyView = requireView().findViewById(R.id.playerPlaylistsEmpty)
        newPlaylistButton = requireView().findViewById(R.id.playerNewPlaylistButton)
        overlayView.alpha = 0f

        playlistsAdapter = PlayerPlaylistsAdapter { playlist ->
            viewModel.onPlaylistSelected(playlist)
        }
        playlistsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        playlistsRecyclerView.adapter = playlistsAdapter

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        overlayView.setOnClickListener {
            hideBottomSheet()
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlayView.alpha = 0f
                        overlayView.visibility = View.GONE
                    }
                    else -> overlayView.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (overlayView.visibility == View.VISIBLE) {
                    val alpha = (slideOffset + 1f) / 2f
                    overlayView.alpha = alpha.coerceIn(0f, 1f)
                }
            }
        })

        newPlaylistButton.setOnClickListener {
            hideBottomSheet()
            findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment)
        }
    }

    private fun showBottomSheet() {
        viewModel.refreshPlaylists()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
        hideBottomSheet()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPlaylists()
    }
} 