package com.example.playlistmaker.features.search.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.data.db.FavoritesDao
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val favoritesDao: FavoritesDao
) : SearchHistoryRepository {
    private val type = object : TypeToken<List<Track>>() {}.type

    override suspend fun getSearchHistory(): List<Track> = withContext(Dispatchers.IO) {
        val json = sharedPreferences.getString("history", "[]")
        val history = try {
            gson.fromJson<List<Track>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        if (history.isEmpty()) {
            emptyList()
        } else {
            val favoriteIds = favoritesDao.getFavoriteTrackIds().toSet()
            history.onEach { track ->
                val artwork: String? = track.artworkUrl512
                if (artwork.isNullOrEmpty()) {
                    track.artworkUrl512 = track.artworkUrl100.replace("100x100", "512x512")
                }
                track.isFavorite = favoriteIds.contains(track.trackId)
            }
        }
    }

    override fun addTrackToHistory(track: Track) {
        val json = sharedPreferences.getString("history", "[]")
        val history = try {
            gson.fromJson<List<Track>>(json, type)?.toMutableList() ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
        if (!history.any { it.trackId == track.trackId }) {
            history.add(0, track)
            if (history.size > 10) {
                history.removeAt(history.lastIndex)
            }
            sharedPreferences.edit().putString("history", gson.toJson(history)).apply()
        }
    }

    override fun clearSearchHistory() {
        sharedPreferences.edit().remove("history").apply()
    }
} 