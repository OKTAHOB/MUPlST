package com.example.playlistmaker.features.player.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.features.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.features.player.domain.usecase.PlayerInteractor

class PlayerViewModelFactory : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            val playerRepository = PlayerRepositoryImpl()
            val playerInteractor = PlayerInteractor(playerRepository)
            
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(playerInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 