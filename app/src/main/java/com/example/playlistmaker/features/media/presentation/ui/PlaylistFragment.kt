package com.example.playlistmaker.features.media.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistState
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistViewModel
import com.google.android.material.button.MaterialButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {
    private val viewModel: PlaylistViewModel by viewModel()
    private lateinit var playlistsAdapter: PlaylistsAdapter

    private lateinit var newPlaylistButton: MaterialButton
    private lateinit var playlistsRecyclerView: RecyclerView
    private lateinit var placeholderIcon: ImageView
    private lateinit var placeholderText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupRecyclerView()
        setupListeners()
        observeState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPlaylists()
    }

    private fun bindViews(view: View) {
        newPlaylistButton = view.findViewById(R.id.newPlaylistButton)
        playlistsRecyclerView = view.findViewById(R.id.playlistsRecyclerView)
        placeholderIcon = view.findViewById(R.id.errorIcon)
        placeholderText = view.findViewById(R.id.errorMessage)
    }

    private fun setupRecyclerView() {
        playlistsAdapter = PlaylistsAdapter()
        playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        playlistsRecyclerView.adapter = playlistsAdapter
    }

    private fun setupListeners() {
        newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
        }
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> {
                playlistsAdapter.submitList(state.playlists)
                playlistsRecyclerView.visibility = View.VISIBLE
                placeholderIcon.visibility = View.GONE
                placeholderText.visibility = View.GONE
            }
            PlaylistState.Empty -> {
                playlistsAdapter.submitList(emptyList())
                playlistsRecyclerView.visibility = View.GONE
                placeholderIcon.visibility = View.VISIBLE
                placeholderText.visibility = View.VISIBLE
            }
        }
    }



    companion object {
        fun newInstance(): PlaylistFragment = PlaylistFragment()
    }
}