package com.example.playlistmaker.features.search.data.mapper

import com.example.playlistmaker.features.search.data.dto.TrackDto
import com.example.playlistmaker.features.search.domain.model.Track

class TrackMapper {
    fun map(trackDto: TrackDto): Track {
        return Track(
            trackId = trackDto.trackId,
            trackName = trackDto.trackName,
            artistName = trackDto.artistName,
            trackTime = trackDto.trackTimeMillis,
            artworkUrl100 = trackDto.artworkUrl100,
            artworkUrl512 = trackDto.artworkUrl100.replace("100x100", "512x512"),
            collectionName = trackDto.collectionName,
            releaseDate = trackDto.releaseDate,
            primaryGenreName = trackDto.primaryGenreName,
            country = trackDto.country,
            previewUrl = trackDto.previewUrl
        )
    }
} 