package com.example.playlistmaker.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackIdConverter {

    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Long>>() {}.type

    @TypeConverter
    fun fromList(trackIds: List<Long>?): String {
        return gson.toJson(trackIds ?: emptyList<Long>())
    }

    @TypeConverter
    fun toList(data: String?): List<Long> {
        if (data.isNullOrBlank()) return emptyList()
        return gson.fromJson(data, typeToken) ?: emptyList()
    }
}
