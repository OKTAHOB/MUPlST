package com.example.playlistmaker.features.search.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {
    private val type = object : TypeToken<List<Track>>() {}.type

    override fun getSearchHistory(): List<Track> {
        val json = sharedPreferences.getString("history", "[]")
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun addTrackToHistory(track: Track) {
        val history = getSearchHistory().toMutableList()
        if (!history.any { it.trackId == track.trackId }) {
            history.add(0, track)
            if (history.size > 10) {
                history.removeAt(history.size - 1)
            }
            val json = gson.toJson(history)
            sharedPreferences.edit().putString("history", json).apply()
        }
    }

    override fun clearSearchHistory() {
        sharedPreferences.edit().remove("history").apply()
    }
} 