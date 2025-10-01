package com.example.playlistmaker.features.media.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.features.media.domain.interactor.FavoritesInteractor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesInteractor.observeFavorites().collect { tracks ->
                if (tracks.isEmpty()) {
                    _state.postValue(FavoritesState.Empty)
                } else {
                    _state.postValue(FavoritesState.Content(tracks))
                }
            }
        }
    }
}