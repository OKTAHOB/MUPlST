package com.example.playlistmaker

import com.example.playlistmaker.retrofit.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.create

object RetrofitClient {
    private const val BASE_URL = "https://itunes.apple.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val musicApiService = retrofit.create<ApiService>()
}