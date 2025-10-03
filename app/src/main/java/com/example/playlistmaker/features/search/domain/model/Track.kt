package com.example.playlistmaker.features.search.domain.model

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: Long,
    val artworkUrl100: String,
    var artworkUrl512: String = artworkUrl100.replace("100x100", "512x512"),
    val collectionName: String?,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    var isFavorite: Boolean = false
)