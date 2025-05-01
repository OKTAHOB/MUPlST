package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()
    private val key = "search_history"

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList().apply {
            removeAll { it.trackId == track.trackId }
            add(0, track)
            if (size > 10) removeLast()
        }
        sharedPreferences.edit()
            .putString(key, Gson().toJson(history))
            .apply()
    }

    fun getHistory(): List<Track> {
        return sharedPreferences.getString(key, null)?.let {
            gson.fromJson(it, object : TypeToken<List<Track>>() {}.type)
        } ?: emptyList()
    }

    fun clear() {
        sharedPreferences.edit().remove(key).apply()
    }
}