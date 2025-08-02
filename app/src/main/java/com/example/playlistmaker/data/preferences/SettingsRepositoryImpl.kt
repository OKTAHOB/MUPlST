package com.example.playlistmaker.data.preferences

import android.content.SharedPreferences
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    override fun getThemeSettings(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun saveThemeSettings(darkThemeEnabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, darkThemeEnabled)
            .apply()
    }

    companion object {
        private const val DARK_THEME_KEY = "dark_theme"
    }
}