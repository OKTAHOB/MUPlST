package com.example.playlistmaker.features.media.domain.model

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackIds: List<Long>,
    val trackCount: Int
)
