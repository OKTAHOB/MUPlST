package com.example.playlistmaker.features.media.data

import com.example.playlistmaker.data.db.PlaylistTracksDao
import com.example.playlistmaker.data.db.PlaylistsDao
import com.example.playlistmaker.features.media.domain.model.Playlist
import com.example.playlistmaker.features.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.features.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistsDao: PlaylistsDao,
    private val playlistTracksDao: PlaylistTracksDao
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist): Long {
        val entity = PlaylistMapper.mapToEntity(playlist)
        return playlistsDao.insertPlaylist(entity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = PlaylistMapper.mapToEntity(playlist)
        playlistsDao.updatePlaylist(entity)
    }

    override fun observePlaylists(): Flow<List<Playlist>> {
        return playlistsDao.observePlaylists().map { entities ->
            entities.map { entity -> PlaylistMapper.mapToDomain(entity) }
        }
    }

    override suspend fun getPlaylistByName(name: String): Playlist? {
        return playlistsDao.getPlaylistByName(name)?.let { entity ->
            PlaylistMapper.mapToDomain(entity)
        }
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return playlistsDao.getPlaylists().map { entity ->
            PlaylistMapper.mapToDomain(entity)
        }
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track) {
        val playlistEntity = playlistsDao.getPlaylistById(playlistId) ?: return
        val updatedTrackIds = playlistEntity.trackIds.toMutableList()
        if (updatedTrackIds.contains(track.trackId)) return
        updatedTrackIds.add(track.trackId)
        val updatedEntity = playlistEntity.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )
        playlistsDao.updatePlaylist(updatedEntity)
        playlistTracksDao.insertTrack(PlaylistTrackMapper.mapToEntity(track))
    }

    override fun observePlaylist(playlistId: Long): Flow<Playlist?> {
        return playlistsDao.observePlaylistById(playlistId).map { entity ->
            entity?.let { PlaylistMapper.mapToDomain(it) }
        }
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistsDao.getPlaylistById(playlistId)?.let { entity ->
            PlaylistMapper.mapToDomain(entity)
        }
    }

    override suspend fun getTracksByIds(trackIds: List<Long>): List<Track> {
        if (trackIds.isEmpty()) return emptyList()
        val allTracks = playlistTracksDao.getAllTracks()
        val tracksById = allTracks.associateBy { it.trackId }
        return trackIds.mapNotNull { id ->
            tracksById[id]?.let { PlaylistTrackMapper.mapToDomain(it) }
        }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        val playlistEntity = playlistsDao.getPlaylistById(playlistId) ?: return
        if (!playlistEntity.trackIds.contains(trackId)) return

        val updatedTrackIds = playlistEntity.trackIds.filterNot { it == trackId }
        val updatedEntity = playlistEntity.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )
        playlistsDao.updatePlaylist(updatedEntity)

        cleanupUnusedTracks(listOf(trackId))
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        val playlistEntity = playlistsDao.getPlaylistById(playlistId) ?: return
        playlistsDao.deletePlaylist(playlistId)
        cleanupUnusedTracks(playlistEntity.trackIds)
    }

    private suspend fun cleanupUnusedTracks(trackIds: Collection<Long>) {
        if (trackIds.isEmpty()) return
        val referencedTrackIds = playlistsDao.getPlaylists()
            .flatMap { it.trackIds }
            .toSet()
        trackIds.forEach { trackId ->
            if (!referencedTrackIds.contains(trackId)) {
                playlistTracksDao.deleteTrack(trackId)
            }
        }
    }
}
