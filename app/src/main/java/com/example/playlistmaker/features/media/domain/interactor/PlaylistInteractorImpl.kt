package com.example.playlistmaker.features.media.domain.interactor

import com.example.playlistmaker.features.media.domain.model.Playlist
import com.example.playlistmaker.features.media.domain.repository.PlaylistCoverStorage
import com.example.playlistmaker.features.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.features.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
    private val coverStorage: PlaylistCoverStorage
) : PlaylistInteractor {

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverUri: String?
    ): Long {
        val trimmedName = name.trim()
        val sanitizedDescription = description?.trim().takeUnless { it.isNullOrEmpty() }
        val storedCoverPath = coverUri
            ?.takeIf { it.isNotBlank() }
            ?.let { uri ->
                withContext(Dispatchers.IO) {
                    coverStorage.copyCoverToStorage(uri, trimmedName)
                }
            }
            ?.takeIf { it.isNotBlank() }

        val playlist = Playlist(
            name = trimmedName,
            description = sanitizedDescription,
            coverPath = storedCoverPath,
            trackIds = emptyList(),
            trackCount = 0
        )
        return withContext(Dispatchers.IO) {
            playlistRepository.createPlaylist(playlist)
        }
    }

    override fun observePlaylists() = playlistRepository.observePlaylists()

    override suspend fun updatePlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            playlistRepository.updatePlaylist(playlist)
        }
    }

    override suspend fun getPlaylistByName(name: String): Playlist? {
        return withContext(Dispatchers.IO) {
            playlistRepository.getPlaylistByName(name.trim())
        }
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return withContext(Dispatchers.IO) {
            playlistRepository.getPlaylists()
        }
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track) {
        withContext(Dispatchers.IO) {
            playlistRepository.addTrackToPlaylist(playlistId, track)
        }
    }

    override fun observePlaylist(playlistId: Long): Flow<Playlist?> {
        return playlistRepository.observePlaylist(playlistId)
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return withContext(Dispatchers.IO) {
            playlistRepository.getPlaylistById(playlistId)
        }
    }

    override suspend fun getTracksByIds(trackIds: List<Long>): List<Track> {
        return withContext(Dispatchers.IO) {
            playlistRepository.getTracksByIds(trackIds)
        }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        withContext(Dispatchers.IO) {
            playlistRepository.removeTrackFromPlaylist(playlistId, trackId)
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        withContext(Dispatchers.IO) {
            playlistRepository.deletePlaylist(playlistId)
        }
    }
}
