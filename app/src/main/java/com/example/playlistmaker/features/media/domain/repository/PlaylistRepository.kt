package com.example.playlistmaker.features.media.domain.repository

import com.example.playlistmaker.features.media.domain.model.Playlist
import com.example.playlistmaker.features.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist): Long
    suspend fun updatePlaylist(playlist: Playlist)
    fun observePlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistByName(name: String): Playlist?
    suspend fun getPlaylists(): List<Playlist>
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track)
}
