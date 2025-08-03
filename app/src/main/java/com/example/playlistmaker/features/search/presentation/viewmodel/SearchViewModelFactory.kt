package com.example.playlistmaker.features.search.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.features.search.data.mapper.TrackMapper
import com.example.playlistmaker.features.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.features.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.features.search.domain.usecase.SearchTracksUseCase

class SearchViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            val trackMapper = TrackMapper()
            val trackRepository = TrackRepositoryImpl(trackMapper)
            val searchHistoryRepository = SearchHistoryRepositoryImpl(
                context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
            )
            val searchTracksUseCase = SearchTracksUseCase(trackRepository, searchHistoryRepository)
            
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchTracksUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 