package com.example.playlistmaker.features.player.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.features.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.features.media.domain.model.Playlist
import com.example.playlistmaker.features.media.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.features.player.service.PlayerServiceController
import com.example.playlistmaker.features.player.service.PlayerServiceState
import com.example.playlistmaker.features.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PlayerViewModel(
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private val _playlistsState = MutableLiveData<PlaylistSelectionState>(PlaylistSelectionState.Empty)
    val playlistsState: LiveData<PlaylistSelectionState> = _playlistsState

    private val _addTrackEvent = MutableLiveData<AddTrackToPlaylistEvent?>()
    val addTrackEvent: LiveData<AddTrackToPlaylistEvent?> = _addTrackEvent

    private var currentTrack: Track? = null
    private var favoriteJob: Job? = null
    private var playlistsJob: Job? = null
    private var serviceStateJob: Job? = null
    private var playerService: PlayerServiceController? = null
    private var isInForeground = true
    private var isPlayerPrepared = false

    init {
        observePlaylists()
    }

    fun bindService(service: PlayerServiceController) {
        playerService = service
        observeServiceState()
        // Если трек уже установлен и плеер еще не подготовлен, подготовим плеер
        if (currentTrack != null && !isPlayerPrepared) {
            preparePlayer()
        }
    }

    fun unbindService() {
        playerService = null
        serviceStateJob?.cancel()
        serviceStateJob = null
        isPlayerPrepared = false
    }

    fun setTrack(track: Track) {
        currentTrack = track
        isPlayerPrepared = false
        _playerState.value = PlayerState(
            track = track,
            playbackState = PlaybackState.TRACK_LOADED,
            currentTime = "00:00",
            isFavorite = track.isFavorite
        )
        // Подготовим плеер только если сервис уже привязан и плеер еще не подготовлен
        if (playerService != null && !isPlayerPrepared) {
            preparePlayer()
        }
        observeFavoriteState(track.trackId)
    }

    fun refreshPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlists = playlistInteractor.getPlaylists()
            emitPlaylistsState(playlists)
        }
    }

    private fun observeServiceState() {
        serviceStateJob?.cancel()
        serviceStateJob = viewModelScope.launch {
            playerService?.playerState?.collect { serviceState ->
                when (serviceState) {
                    is PlayerServiceState.Idle -> {
                        isPlayerPrepared = false
                    }
                    is PlayerServiceState.Prepared -> {
                        isPlayerPrepared = true
                        _playerState.value = _playerState.value?.copy(
                            playbackState = PlaybackState.PREPARED
                        )
                    }
                    is PlayerServiceState.Playing -> {
                        _playerState.value = _playerState.value?.copy(
                            playbackState = PlaybackState.PLAYING,
                            currentTime = formatTime(serviceState.currentPosition)
                        )
                        handleForegroundState(true)
                    }
                    is PlayerServiceState.Paused -> {
                        _playerState.value = _playerState.value?.copy(
                            playbackState = PlaybackState.PAUSED,
                            currentTime = formatTime(serviceState.currentPosition)
                        )
                        handleForegroundState(false)
                    }
                    is PlayerServiceState.Completed -> {
                        _playerState.value = _playerState.value?.copy(
                            playbackState = PlaybackState.COMPLETED,
                            currentTime = "00:00"
                        )
                        handleForegroundState(false)
                    }
                }
            }
        }
    }

    private fun formatTime(position: Int): String {
        val minutes = position / 60000
        val seconds = (position % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun preparePlayer() {
        currentTrack?.let { track ->
            playerService?.preparePlayer(
                track.previewUrl,
                onPrepared = {},
                onCompletion = {}
            )
        }
    }

    fun togglePlayback() {
        val currentState = _playerState.value?.playbackState
        when (currentState) {
            PlaybackState.PLAYING -> {
                playerService?.pausePlayer()
            }
            PlaybackState.PREPARED,
            PlaybackState.PAUSED,
            PlaybackState.COMPLETED -> {
                playerService?.startPlayer()
            }
            else -> {
                // Если плеер не готов, ничего не делаем
            }
        }
    }

    private fun handleForegroundState(isPlaying: Boolean) {
        if (!isInForeground && isPlaying) {
            playerService?.showForegroundNotification()
        } else {
            playerService?.hideForegroundNotification()
        }
    }

    fun onAppInForeground() {
        isInForeground = true
        playerService?.hideForegroundNotification()
    }

    fun onAppInBackground() {
        isInForeground = false
        val currentState = _playerState.value?.playbackState
        if (currentState == PlaybackState.PLAYING) {
            playerService?.showForegroundNotification()
        }
    }

    fun onFavoriteClicked() {
        val track = currentTrack ?: return
        val newState = !track.isFavorite
        viewModelScope.launch(Dispatchers.IO) {
            if (newState) {
                favoritesInteractor.addToFavorites(track)
            } else {
                favoritesInteractor.removeFromFavorites(track.trackId)
            }
        }
        updateFavoriteState(newState)
    }

    private fun observeFavoriteState(trackId: Long) {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            favoritesInteractor.observeFavorites()
                .map { tracks -> tracks.any { it.trackId == trackId } }
                .distinctUntilChanged()
                .collect { isFavorite ->
                    updateFavoriteState(isFavorite)
                }
        }
    }

    private fun observePlaylists() {
        playlistsJob?.cancel()
        playlistsJob = viewModelScope.launch {
            playlistInteractor.observePlaylists().collect { playlists ->
                emitPlaylistsState(playlists)
            }
        }
    }

    fun onPlaylistSelected(playlist: Playlist) {
        val track = currentTrack ?: return
        if (playlist.trackIds.contains(track.trackId)) {
            _addTrackEvent.value = AddTrackToPlaylistEvent.AlreadyExists(playlist.name)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.addTrackToPlaylist(playlist.id, track)
            _addTrackEvent.postValue(AddTrackToPlaylistEvent.Added(playlist.name))
        }
    }

    fun onAddTrackEventConsumed() {
        _addTrackEvent.value = null
    }

    private fun emitPlaylistsState(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            _playlistsState.postValue(PlaylistSelectionState.Empty)
        } else {
            _playlistsState.postValue(PlaylistSelectionState.Content(playlists))
        }
    }

    private fun updateFavoriteState(isFavorite: Boolean) {
        currentTrack = currentTrack?.copy(isFavorite = isFavorite)
        _playerState.postValue(
            _playerState.value?.copy(
                track = currentTrack,
                isFavorite = isFavorite
            ) ?: PlayerState(track = currentTrack, isFavorite = isFavorite)
        )
    }

    override fun onCleared() {
        super.onCleared()
        serviceStateJob?.cancel()
        favoriteJob?.cancel()
        playlistsJob?.cancel()
    }
}

enum class PlaybackState {
    TRACK_LOADED,
    PREPARED,
    PLAYING,
    PAUSED,
    COMPLETED
}

data class PlayerState(
    val track: Track? = null,
    val playbackState: PlaybackState = PlaybackState.TRACK_LOADED,
    val currentTime: String = "00:00",
    val isFavorite: Boolean = false
)
