package com.example.playlistmaker.features.search.data.network

import com.example.playlistmaker.features.search.data.dto.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/search")
    suspend fun search(
        @Query("term") term: String,
        @Query("entity") entity: String = "song"
    ): SearchResponse
}