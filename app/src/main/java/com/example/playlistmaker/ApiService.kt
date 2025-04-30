package com.example.playlistmaker.retrofit


import com.example.playlistmaker.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("/search")
    fun search(
        @Query("term") term: String,
        @Query("entity") entity: String = "song"
    ): Call<SearchResponse>
}