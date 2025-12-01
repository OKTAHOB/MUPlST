package com.example.playlistmaker.features.player.presentation.ui.compose

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.features.media.domain.model.Playlist
import com.example.playlistmaker.features.media.presentation.ui.compose.PlaylistItem
import com.example.playlistmaker.features.player.presentation.viewmodel.AddTrackToPlaylistEvent
import com.example.playlistmaker.features.player.presentation.viewmodel.PlaybackState
import com.example.playlistmaker.features.player.presentation.viewmodel.PlayerState
import com.example.playlistmaker.features.player.presentation.viewmodel.PlayerViewModel
import com.example.playlistmaker.features.player.presentation.viewmodel.PlaylistSelectionState
import com.example.playlistmaker.features.player.service.PlayerService
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.ui.components.AppButton
import com.example.playlistmaker.ui.components.AppTopBar
import com.example.playlistmaker.ui.navigation.NavRoutes
import com.example.playlistmaker.ui.theme.Typography
import com.example.playlistmaker.ui.theme.YpBlue
import com.example.playlistmaker.ui.theme.YpGray
import com.example.playlistmaker.ui.theme.YpRed
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    trackJson: String,
    navController: NavHostController,
    viewModel: PlayerViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val track = remember { Gson().fromJson(trackJson, Track::class.java) }
    
    val playerState by viewModel.playerState.observeAsState(
        PlayerState(track = track, playbackState = PlaybackState.TRACK_LOADED)
    )
    val playlistsState by viewModel.playlistsState.observeAsState(PlaylistSelectionState.Empty)
    val addTrackEvent by viewModel.addTrackEvent.observeAsState()
    
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(track) {
        viewModel.setTrack(track)
    }

    DisposableEffect(context) {
        var playerService: com.example.playlistmaker.features.player.service.PlayerServiceController? = null
        var isBound = false

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as PlayerService.PlayerBinder
                playerService = binder.getService()
                isBound = true
                playerService?.let { viewModel.bindService(it) }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
                playerService = null
            }
        }

        val intent = Intent(context, PlayerService::class.java).apply {
            putExtra(PlayerService.EXTRA_TRACK_URL, track.previewUrl)
            putExtra(PlayerService.EXTRA_TRACK_NAME, track.trackName)
            putExtra(PlayerService.EXTRA_ARTIST_NAME, track.artistName)
        }
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        onDispose {
            playerService?.let { service ->
                val currentState = viewModel.playerState.value?.playbackState
                if (currentState == PlaybackState.PLAYING) {
                    service.pausePlayer()
                }
                service.releasePlayer()
                service.hideForegroundNotification()
            }
            if (isBound) {
                viewModel.unbindService()
                context.unbindService(serviceConnection)
            }
        }
    }

    LaunchedEffect(addTrackEvent) {
        when (val event = addTrackEvent) {
            is AddTrackToPlaylistEvent.Added -> {
                Toast.makeText(
                    context,
                    "Добавлено в плейлист ${event.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
                showBottomSheet = false
                viewModel.onAddTrackEventConsumed()
            }
            is AddTrackToPlaylistEvent.AlreadyExists -> {
                Toast.makeText(
                    context,
                    "Трек уже добавлен в плейлист ${event.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.onAddTrackEventConsumed()
            }
            null -> {}
        }
    }

    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            AppTopBar(
                showBackButton = true,
                title = "",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 26.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    model = track.artworkUrl100.replace("100x100", "512x512"),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.img_placeholder)
                )
                Text(
                    modifier = Modifier.padding(top = 26.dp),
                    text = track.trackName
                )
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = track.artistName
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.clickable {
                            viewModel.refreshPlaylists()
                            showBottomSheet = true
                        },
                        painter = painterResource(R.drawable.add_playlist_button),
                        contentDescription = null,
                        tint = YpGray
                    )

                    PlayButton(
                        isPlaying = playerState.playbackState == PlaybackState.PLAYING,
                        isEnabled = true,
                        onClick = { viewModel.togglePlayback() }
                    )

                    Icon(
                        modifier = Modifier.clickable {
                            viewModel.onFavoriteClicked()
                        },
                        painter = if (playerState.isFavorite) {
                            painterResource(R.drawable.like_button_active)
                        } else {
                            painterResource(R.drawable.like_button)
                        },
                        contentDescription = null,
                        tint = if (playerState.isFavorite) YpRed else YpGray
                    )
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    text = playerState.currentTime,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 30.dp)
            ) {
                TrackInfoRow("Длительность", formatTrackTime(track.trackTime))
                track.collectionName?.takeIf { it.isNotEmpty() }?.let {
                    TrackInfoRow("Альбом", it)
                }
                TrackInfoRow("Год", track.releaseDate.split("-").firstOrNull() ?: track.releaseDate)
                TrackInfoRow("Жанр", track.primaryGenreName)
                TrackInfoRow("Страна", track.country)
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                PlaylistSelectionContent(
                    state = playlistsState,
                    onPlaylistSelected = { playlist ->
                        viewModel.onPlaylistSelected(playlist)
                    },
                    onNewPlaylistClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                            navController.navigate(NavRoutes.CreatePlaylist.route)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PlayButton(
    isPlaying: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == com.example.playlistmaker.ui.theme.DarkBackground
    val playIcon = if (isDark) R.drawable.audioplayer_button_play_dark else R.drawable.audioplayer_button_play_light
    val pauseIcon = if (isDark) R.drawable.audioplayer_button_pause_dark else R.drawable.audioplayer_button_pause_light
    
    Icon(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .size(100.dp)
            .clickable(enabled = isEnabled, onClick = onClick),
        painter = painterResource(if (isPlaying) pauseIcon else playIcon),
        contentDescription = if (isPlaying) "Пауза" else "Воспроизведение",
        tint = androidx.compose.ui.graphics.Color.Unspecified
    )
}

@Composable
private fun TrackInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = YpGray,
            style = Typography.titleSmall
        )
        Text(
            text = value,
            style = Typography.titleSmall
        )
    }
}

@Composable
private fun PlaylistSelectionContent(
    state: PlaylistSelectionState,
    onPlaylistSelected: (Playlist) -> Unit,
    onNewPlaylistClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Добавить в плейлист",
            style = Typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        AppButton(
            text = "Новый плейлист",
            onClick = onNewPlaylistClick,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (state) {
            is PlaylistSelectionState.Content -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(state.playlists) { playlist ->
                        PlaylistSelectionItem(
                            playlist = playlist,
                            onClick = { onPlaylistSelected(playlist) }
                        )
                    }
                }
            }
            PlaylistSelectionState.Empty -> {
                Text(
                    text = "У вас пока нет плейлистов",
                    style = Typography.bodyMedium,
                    color = YpGray,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun PlaylistSelectionItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = playlist.coverPath,
            contentDescription = playlist.name,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = playlist.name,
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = getTracksCountText(playlist.trackCount),
                style = Typography.bodySmall,
                color = YpGray
            )
        }
    }
}

private fun formatTrackTime(millis: Long): String {
    val minutes = millis / 60000
    val seconds = (millis % 60000) / 1000
    return String.format("%02d:%02d", minutes, seconds)
}

private fun getTracksCountText(count: Int): String {
    return when {
        count % 100 in 11..19 -> "$count треков"
        count % 10 == 1 -> "$count трек"
        count % 10 in 2..4 -> "$count трека"
        else -> "$count треков"
    }
}
