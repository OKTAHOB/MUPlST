package com.example.playlistmaker.features.media.presentation.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.features.media.domain.model.Playlist
import com.example.playlistmaker.features.media.presentation.viewmodel.CreatePlaylistViewModel
import com.example.playlistmaker.features.media.presentation.viewmodel.EditPlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel: CreatePlaylistViewModel by viewModel<EditPlaylistViewModel>()

    private val editViewModel: EditPlaylistViewModel
        get() = viewModel as EditPlaylistViewModel

    private var hasAppliedInitialData = false
    private var coverChanged = false

    override fun getTitleTextRes(): Int = R.string.edit_playlist

    override fun getActionButtonTextRes(): Int = R.string.save

    override fun onFragmentReady() {
        super.onFragmentReady()
        observePlaylist()
        val playlistId = arguments?.getLong(ARG_PLAYLIST_ID)
        if (playlistId == null) {
            findNavController().navigateUp()
        } else {
            editViewModel.loadPlaylist(playlistId)
        }
    }

    private fun observePlaylist() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editViewModel.playlistState.collect { playlist ->
                    if (playlist != null) {
                        applyPlaylistData(playlist)
                    }
                }
            }
        }
    }

    private fun applyPlaylistData(playlist: Playlist) {
        if (hasAppliedInitialData) return
        hasAppliedInitialData = true

        nameEditText.setText(playlist.name)
        descriptionEditText.setText(playlist.description.orEmpty())

        val coverUri = playlist.coverPath?.let { Uri.parse(it) }
        applyCoverUri(coverUri)
        coverChanged = false
    }

    override fun onCreateButtonClicked(name: String, description: String?, coverUri: String?) {
        editViewModel.savePlaylist(name, description, coverUri, coverChanged)
    }

    override fun onCoverChanged() {
        coverChanged = true
    }

    override fun handleExit() {
        findNavController().navigateUp()
    }

    override fun onPlaylistSaved(playlistName: String) {
        findNavController().navigateUp()
    }

    override fun onPlaylistSaveError() {
        toast(R.string.playlist_update_error)
    }

    override fun onPlaylistNotFound() {
        toast(R.string.playlist_not_found)
        findNavController().navigateUp()
    }

    private fun toast(messageRes: Int) {
        if (isAdded) {
            Toast.makeText(requireContext(), getString(messageRes), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val ARG_PLAYLIST_ID = "playlistId"
    }
}
