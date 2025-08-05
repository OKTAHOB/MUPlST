package com.example.playlistmaker.features.media.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistViewModel
import com.example.playlistmaker.R

class PlaylistFragment : Fragment() {
    private val viewModel: PlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    companion object {
        fun newInstance(): PlaylistFragment = PlaylistFragment()
    }
}