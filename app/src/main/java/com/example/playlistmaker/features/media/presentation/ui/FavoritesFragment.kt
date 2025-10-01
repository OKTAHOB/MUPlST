package com.example.playlistmaker.features.media.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.features.media.presentation.viewmodel.FavoritesState
import com.example.playlistmaker.features.media.presentation.viewmodel.FavoritesViewModel
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.presentation.adapter.SearchAdapter
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()

    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderGroup: Group
    private lateinit var adapter: SearchAdapter

    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_track, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.favoritesRecyclerView)
        placeholderGroup = view.findViewById(R.id.favoritesPlaceholderGroup)

        adapter = SearchAdapter(emptyList(), ::handleTrackClick)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> {
                placeholderGroup.isVisible = false
                recyclerView.isVisible = true
                adapter.updateData(state.tracks)
            }
            FavoritesState.Empty -> {
                recyclerView.isVisible = false
                placeholderGroup.isVisible = true
            }
        }
    }

    private fun handleTrackClick(track: Track) {
        if (!clickDebounce()) return
        val trackJson = Gson().toJson(track)
        val args = Bundle().apply {
            putString("trackJson", trackJson)
        }
        findNavController().navigate(R.id.action_mediaLibraryFragment_to_playerFragment, args)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun onDestroyView() {
        if (::recyclerView.isInitialized) {
            recyclerView.adapter = null
        }
        isClickAllowed = true
        super.onDestroyView()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }
}
