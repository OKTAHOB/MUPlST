package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean("dark_theme", false)
        applyTheme()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        applyTheme()
        saveTheme()
    }

    private fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun saveTheme() {
        getSharedPreferences("settings", MODE_PRIVATE)
            .edit()
            .putBoolean("dark_theme", darkTheme)
            .apply()
    }
}