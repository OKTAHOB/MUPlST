package com.example.playlistmaker.features.media.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.features.media.presentation.viewmodel.MediaLibraryViewModel
import androidx.viewpager2.widget.ViewPager2
import android.widget.ImageButton
import android.widget.TextView

class MediaLibraryActivity : AppCompatActivity() {
    private val viewModel: MediaLibraryViewModel by viewModel()
    private lateinit var viewPager: ViewPager2
    private lateinit var tabTitles: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        tabTitles = arrayOf(
            getString(R.string.tab_favorites),
            getString(R.string.tab_playlists)
        )

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = MediaLibraryPagerAdapter(this)

        val tabLayout = findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)
        com.google.android.material.tabs.TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.backImageView)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
