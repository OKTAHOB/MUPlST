package com.example.playlistmaker.features.main.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.features.search.presentation.ui.SearchActivity
import com.example.playlistmaker.features.settings.presentation.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        setupButtons()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.srch_btn).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<Button>(R.id.media_btn).setOnClickListener {
            startActivity(Intent(this, com.example.playlistmaker.features.media.presentation.ui.MediaLibraryActivity::class.java))        }

        findViewById<Button>(R.id.set_btn).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
} 