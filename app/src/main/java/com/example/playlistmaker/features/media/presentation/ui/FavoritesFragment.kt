package com.example.playlistmaker.features.media.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.features.media.presentation.viewmodel.FavoritesViewModel
import com.example.playlistmaker.R

class FavoritesFragment : Fragment() {
    private val viewModel: FavoritesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_track, container, false)
    }

    companion object {
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }
}
