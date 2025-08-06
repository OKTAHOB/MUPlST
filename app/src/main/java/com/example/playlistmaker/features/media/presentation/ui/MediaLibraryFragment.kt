package com.example.playlistmaker.features.media.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.features.media.presentation.viewmodel.MediaLibraryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.viewpager2.widget.ViewPager2

class MediaLibraryFragment : Fragment() {
    private val viewModel: MediaLibraryViewModel by viewModel()
    private lateinit var viewPager: ViewPager2
    private lateinit var tabTitles: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_media_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        tabTitles = arrayOf(
            getString(R.string.tab_favorites),
            getString(R.string.tab_playlists)
        )

        viewPager = requireView().findViewById(R.id.viewPager)
        viewPager.adapter = MediaLibraryPagerAdapter(this)

        val tabLayout = requireView().findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
} 