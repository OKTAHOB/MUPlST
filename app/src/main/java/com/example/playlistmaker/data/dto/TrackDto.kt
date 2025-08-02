package com.example.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName

data class TrackDto(
    @SerializedName("trackId") val trackId: Long,
    @SerializedName("trackName") val trackName: String?,
    @SerializedName("artistName") val artistName: String?,
    @SerializedName("trackTimeMillis") val trackTime: Long?,
    @SerializedName("artworkUrl100") val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) {
    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
}