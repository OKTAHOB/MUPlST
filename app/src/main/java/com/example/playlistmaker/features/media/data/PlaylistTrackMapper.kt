package com.example.playlistmaker.features.media.data

import com.example.playlistmaker.data.db.PlaylistTrackEntity
import com.example.playlistmaker.features.search.domain.model.Track

object PlaylistTrackMapper {

    fun mapToEntity(track: Track): PlaylistTrackEntity {
        val artworkUrl512 = track.artworkUrl512.ifEmpty {
            track.artworkUrl100.replace("100x100", "512x512")
        }
        return PlaylistTrackEntity(
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
            previewUrl = track.previewUrl
        )
    }
}
