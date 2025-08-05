package com.example.playlistmaker.features.media.presentation.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediaLibraryPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> FavoritesFragment.newInstance()
            1 -> PlaylistFragment.newInstance()
            else -> throw IllegalArgumentException()
        }
}