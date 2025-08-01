package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.model.Track

class TrackMapper {
    fun map(dto: TrackDto): Track {
        return Track(
            trackId = dto.trackId,
            trackName = dto.trackName ?: "",
            artistName = dto.artistName ?: "",
            trackTime = dto.trackTime ?: 0,
            artworkUrl100 = dto.artworkUrl100 ?: "",
            collectionName = dto.collectionName,
            releaseDate = dto.releaseDate,
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl
        )
    }
}