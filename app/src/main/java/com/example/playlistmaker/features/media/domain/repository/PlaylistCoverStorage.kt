package com.example.playlistmaker.features.media.domain.repository

interface PlaylistCoverStorage {
    suspend fun copyCoverToStorage(sourceUri: String, playlistName: String): String
}
