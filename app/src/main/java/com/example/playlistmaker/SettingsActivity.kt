package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backArrow = findViewById<MaterialToolbar>(R.id.settings_appbar)

        backArrow.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}