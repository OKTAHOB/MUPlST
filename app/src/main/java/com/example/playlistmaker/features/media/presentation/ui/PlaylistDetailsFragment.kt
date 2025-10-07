package com.example.playlistmaker.features.media.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistDetailsEvent
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistDetailsState
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistDetailsViewModel
import com.example.playlistmaker.features.search.domain.model.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import java.util.concurrent.TimeUnit

class PlaylistDetailsFragment : Fragment() {

    private val viewModel: PlaylistDetailsViewModel by viewModel()

    private lateinit var coverImage: ImageView
    private lateinit var backButton: ImageView
    private lateinit var playlistName: TextView
    private lateinit var playlistDescription: TextView
    private lateinit var playlistDuration: TextView
    private lateinit var playlistTrackCount: TextView
    private lateinit var shareButton: ImageView
    private lateinit var menuButton: ImageView
    private lateinit var emptyTitle: TextView
    private lateinit var emptyImage: ImageView
    private lateinit var tracksRecycler: RecyclerView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var notFoundText: TextView
    private lateinit var contentContainer: View
    private lateinit var bottomSheet: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheet: View
    private lateinit var menuOverlay: View
    private lateinit var menuBehavior: BottomSheetBehavior<View>
    private lateinit var menuShare: View
    private lateinit var menuEdit: View
    private lateinit var menuDelete: View
    private lateinit var menuCover: ImageView
    private lateinit var menuName: TextView
    private lateinit var menuTrackCount: TextView

    private lateinit var tracksAdapter: PlaylistTracksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_playlist_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupListeners()
        observeState()

        val playlistId = arguments?.getLong(ARG_PLAYLIST_ID)
        if (playlistId == null) {
            findNavController().popBackStack()
        } else {
            viewModel.loadPlaylist(playlistId)
        }
    }

    private fun bindViews(view: View) {
        coverImage = view.findViewById(R.id.ivPlaylistCover)
        backButton = view.findViewById(R.id.ivBack)
        playlistName = view.findViewById(R.id.tvPlaylistName)
        playlistDescription = view.findViewById(R.id.tvPlaylistDescription)
        playlistDuration = view.findViewById(R.id.tvPlaylistDuration)
        playlistTrackCount = view.findViewById(R.id.tvPlaylistTrackCount)
        shareButton = view.findViewById(R.id.ivShare)
        menuButton = view.findViewById(R.id.ivMenu)
        emptyTitle = view.findViewById(R.id.tvEmptyTracks)
        emptyImage = view.findViewById(R.id.ivEmptyState)
        tracksRecycler = view.findViewById(R.id.rvPlaylistTracks)
        loadingProgress = view.findViewById(R.id.pbLoading)
        notFoundText = view.findViewById(R.id.tvPlaylistNotFound)
        contentContainer = view.findViewById(R.id.contentContainer)
        bottomSheet = view.findViewById(R.id.bottomSheetTracks)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            isHideable = false
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        menuBottomSheet = view.findViewById(R.id.menuBottomSheet)
        menuOverlay = view.findViewById(R.id.menuOverlay)
        menuShare = view.findViewById(R.id.menuShare)
        menuEdit = view.findViewById(R.id.menuEdit)
        menuDelete = view.findViewById(R.id.menuDelete)
        menuCover = view.findViewById(R.id.ivMenuCover)
        menuName = view.findViewById(R.id.tvMenuPlaylistName)
        menuTrackCount = view.findViewById(R.id.tvMenuTrackCount)
        menuBehavior = BottomSheetBehavior.from(menuBottomSheet).apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheetView: View, newState: Int) {
                    val isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
                    menuOverlay.isVisible = isVisible
                    if (!isVisible) {
                        menuOverlay.alpha = 0f
                        bottomSheet.alpha = 1f
                    }
                }

                override fun onSlide(bottomSheetView: View, slideOffset: Float) {
                    if (slideOffset >= 0f) {
                        val offset = slideOffset.coerceIn(0f, 1f)
                        menuOverlay.alpha = offset
                        bottomSheet.alpha = 1f - (0.3f * offset)
                    }
                }
            })
        }

        tracksAdapter = PlaylistTracksAdapter(
            onTrackClick = ::openPlayer,
            onTrackLongClick = ::showDeleteTrackDialog
        )
        tracksRecycler.layoutManager = LinearLayoutManager(requireContext())
        tracksRecycler.adapter = tracksAdapter
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        shareButton.setOnClickListener { sharePlaylist() }
        menuButton.setOnClickListener { toggleMenu(true) }
        menuOverlay.setOnClickListener { toggleMenu(false) }
        menuShare.setOnClickListener {
            toggleMenu(false)
            sharePlaylist()
        }
        menuEdit.setOnClickListener {
            toggleMenu(false)
            viewModel.getCurrentContent()?.let { content ->
                val bundle = Bundle().apply {
                    putLong(EditPlaylistFragment.ARG_PLAYLIST_ID, content.playlist.id)
                }
                findNavController().navigate(R.id.action_playlistDetailsFragment_to_editPlaylistFragment, bundle)
            }
        }
        menuDelete.setOnClickListener {
            toggleMenu(false)
            confirmDeletePlaylist()
        }
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                PlaylistDetailsState.Loading -> renderLoading()
                PlaylistDetailsState.NotFound -> renderNotFound()
                is PlaylistDetailsState.Content -> renderContent(state)
            }
        }
        viewModel.events.observe(viewLifecycleOwner) { event ->
            when (event) {
                PlaylistDetailsEvent.PlaylistDeleted -> {
                    findNavController().popBackStack(R.id.mediaLibraryFragment, false)
                }
            }
        }
    }

    private fun renderLoading() {
        loadingProgress.isVisible = true
        contentContainer.isVisible = false
        bottomSheet.isVisible = false
        notFoundText.isVisible = false
    }

    private fun renderNotFound() {
        loadingProgress.isVisible = false
        contentContainer.isVisible = false
        bottomSheet.isVisible = false
        notFoundText.isVisible = true
    }

    private fun renderContent(state: PlaylistDetailsState.Content) {
        loadingProgress.isVisible = false
        notFoundText.isVisible = false
        contentContainer.isVisible = true
        bottomSheet.isVisible = true

        val playlist = state.playlist

        if (!playlist.coverPath.isNullOrBlank()) {
            Glide.with(coverImage)
                .load(playlist.coverPath)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(coverImage)
        } else {
            coverImage.setImageResource(R.drawable.placeholder)
        }

        playlistName.text = playlist.name

        if (playlist.description.isNullOrBlank()) {
            playlistDescription.isVisible = false
        } else {
            playlistDescription.isVisible = true
            playlistDescription.text = playlist.description
        }

        val totalDurationMinutes = calculateTotalMinutes(state.totalDurationMillis, state.tracks.size)
        playlistDuration.text = resources.getQuantityString(
            R.plurals.playlist_minutes,
            totalDurationMinutes,
            totalDurationMinutes
        )

        val trackCount = state.tracks.size
        playlistTrackCount.text = resources.getQuantityString(
            R.plurals.playlist_track_count,
            trackCount,
            trackCount
        )

        tracksAdapter.submitList(state.tracks)
        val hasTracks = trackCount > 0
        tracksRecycler.isVisible = hasTracks
        emptyTitle.isVisible = !hasTracks
        emptyImage.isVisible = !hasTracks

        bindMenuInfo(state)

        if (hasTracks) {
            updateBottomSheetPeekHeight()
        }
    }

    private fun bindMenuInfo(state: PlaylistDetailsState.Content) {
        val playlist = state.playlist
        if (!playlist.coverPath.isNullOrBlank()) {
            Glide.with(menuCover)
                .load(playlist.coverPath)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(menuCover)
        } else {
            menuCover.setImageResource(R.drawable.placeholder)
        }

        menuName.text = playlist.name

        val trackCount = state.tracks.size
        menuTrackCount.text = resources.getQuantityString(
            R.plurals.playlist_track_count,
            trackCount,
            trackCount
        )
    }

    private fun calculateTotalMinutes(totalMillis: Long, trackCount: Int): Int {
        if (totalMillis <= 0L) {
            return if (trackCount > 0) 1 else 0
        }
        val minutes = TimeUnit.MILLISECONDS.toMinutes(totalMillis).toInt()
        return if (minutes <= 0 && trackCount > 0) 1 else minutes
    }

    private fun updateBottomSheetPeekHeight() {
        bottomSheet.post {
            val parentHeight = requireView().height
            if (parentHeight <= 0) return@post

            val shareLocation = IntArray(2)
            shareButton.getLocationOnScreen(shareLocation)
            val rootLocation = IntArray(2)
            requireView().getLocationOnScreen(rootLocation)
            val shareTop = shareLocation[1] - rootLocation[1]
            val shareHeight = if (shareButton.height > 0) shareButton.height else shareButton.measuredHeight
            val params = shareButton.layoutParams as? MarginLayoutParams
            val shareBottomMargin = params?.bottomMargin ?: 0
            val spacing = resources.getDimensionPixelOffset(R.dimen.std_padding)
            val collapsedTop = shareTop + shareHeight + shareBottomMargin + spacing
            val peekHeight = (parentHeight - collapsedTop).coerceAtLeast(0)
            bottomSheetBehavior.peekHeight = peekHeight

            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun openPlayer(track: Track) {
        val trackJson = Gson().toJson(track)
        val bundle = Bundle().apply {
            putString("trackJson", trackJson)
        }
        findNavController().navigate(R.id.action_playlistDetailsFragment_to_playerFragment, bundle)
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.playlist_delete_track_message))
            .setNegativeButton(R.string.playlist_delete_track_negative, null)
            .setPositiveButton(R.string.playlist_delete_track_positive) { _, _ ->
                viewModel.removeTrack(track.trackId)
            }
            .show()
    }

    private fun sharePlaylist() {
        val content = viewModel.getCurrentContent()
        if (content == null) {
            Toast.makeText(requireContext(), R.string.playlist_not_found, Toast.LENGTH_SHORT).show()
            return
        }

        val tracks = content.tracks
        if (tracks.isEmpty()) {
            Toast.makeText(requireContext(), R.string.playlist_empty_share_toast, Toast.LENGTH_SHORT).show()
            return
        }

        val descriptionLine = content.playlist.description?.takeIf { it.isNotBlank() }
        val trackCountLine = resources.getQuantityString(
            R.plurals.playlist_share_track_count,
            tracks.size,
            tracks.size
        )
        val shareText = buildString {
            append(content.playlist.name).append('\n')
            descriptionLine?.let {
                append(it).append('\n')
            }
            append(trackCountLine).append('\n')
            tracks.forEachIndexed { index, track ->
                val minutes = TimeUnit.MILLISECONDS.toMinutes(track.trackTime)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(track.trackTime) - minutes * 60
                val formattedTime = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
                append(String.format(Locale.getDefault(), "%d. %s - %s (%s)", index + 1, track.artistName, track.trackName, formattedTime))
                if (index != tracks.lastIndex) {
                    append('\n')
                }
            }
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)))
        }
    }

    private fun toggleMenu(show: Boolean) {
        if (show) {
            menuOverlay.isVisible = true
            menuOverlay.alpha = 0f
            bottomSheet.alpha = 1f
            menuBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            menuOverlay.isVisible = false
        }
    }

    private fun confirmDeletePlaylist() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.playlist_delete_dialog_title)
            .setMessage(R.string.playlist_delete_dialog_message)
            .setNegativeButton(R.string.playlist_delete_dialog_negative, null)
            .setPositiveButton(R.string.playlist_delete_dialog_positive) { _, _ ->
                viewModel.deletePlaylist()
            }
            .show()
    }

    companion object {
        const val ARG_PLAYLIST_ID = "playlistId"
    }
}
