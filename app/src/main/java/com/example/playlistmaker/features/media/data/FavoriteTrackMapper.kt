package com.example.playlistmaker.features.media.data

import com.example.playlistmaker.data.db.FavoriteTrackEntity
import com.example.playlistmaker.features.search.domain.model.Track

object FavoriteTrackMapper {

    fun mapToEntity(track: Track): FavoriteTrackEntity {
        val artworkUrl512 = track.artworkUrl512.ifEmpty {
            track.artworkUrl100.replace("100x100", "512x512")
        }
        return FavoriteTrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            artworkUrl512 = artworkUrl512,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTime,
            previewUrl = track.previewUrl,
            addedAt = System.currentTimeMillis()
        )
    }

    fun mapToDomain(entity: FavoriteTrackEntity): Track {
        val artworkUrl512 = if (entity.artworkUrl512.isBlank()) {
            entity.artworkUrl100.replace("100x100", "512x512")
        } else {
            entity.artworkUrl512
        }
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTime = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100,
            artworkUrl512 = artworkUrl512,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl,
            isFavorite = true
        )
    }
}
