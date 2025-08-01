package com.example.playlistmaker.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupWindowInsets()
        setupNavigationButtons()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<LinearLayout>(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupNavigationButtons() {
        findViewById<Button>(R.id.srch_btn).setOnClickListener {
            navigateTo(SearchActivity::class.java)
        }

        findViewById<Button>(R.id.media_btn).setOnClickListener {
            navigateTo(LibraryActivity::class.java)
        }

        findViewById<Button>(R.id.set_btn).setOnClickListener {
            navigateTo(SettingsActivity::class.java)
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }
}