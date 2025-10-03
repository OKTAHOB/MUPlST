package com.example.playlistmaker.features.media.data

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.features.media.domain.model.Playlist

object PlaylistMapper {

    fun mapToEntity(playlist: Playlist): PlaylistEntity {
        val trackIds = playlist.trackIds.ifEmpty { emptyList() }
        val trackCount = trackIds.size
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = trackIds,
            trackCount = trackCount
        )
    }

    fun mapToDomain(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverPath = entity.coverPath,
            trackIds = entity.trackIds,
            trackCount = entity.trackCount
        )
    }
}
