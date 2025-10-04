package com.example.playlistmaker.features.media.domain.interactor

import com.example.playlistmaker.features.media.domain.model.Playlist
import com.example.playlistmaker.features.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String?, coverUri: String?): Long
    fun observePlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getPlaylistByName(name: String): Playlist?
    suspend fun getPlaylists(): List<Playlist>
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track)
}
