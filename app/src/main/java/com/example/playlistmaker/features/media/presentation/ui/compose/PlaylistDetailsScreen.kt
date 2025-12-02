package com.example.playlistmaker.features.media.presentation.ui.compose

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.features.media.domain.model.Playlist
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistDetailsEvent
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistDetailsState
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistDetailsViewModel
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.ui.components.AppTopBar
import com.example.playlistmaker.ui.components.PlaceholderError
import com.example.playlistmaker.ui.components.PlaceholderType
import com.example.playlistmaker.ui.components.TrackItem
import com.example.playlistmaker.ui.navigation.NavRoutes
import com.example.playlistmaker.ui.theme.Typography
import com.example.playlistmaker.ui.theme.YpGray
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailsScreen(
    playlistId: Long,
    navController: NavHostController,
    viewModel: PlaylistDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.observeAsState(PlaylistDetailsState.Loading)
    val events by viewModel.events.observeAsState()

    var showMenuSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteTrackDialog by remember { mutableStateOf<Track?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(playlistId) {
        viewModel.loadPlaylist(playlistId)
    }

    LaunchedEffect(events) {
        when (events) {
            PlaylistDetailsEvent.PlaylistDeleted -> {
                navController.popBackStack()
            }
            null -> {}
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                showBackButton = true,
                title = "",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        when (val currentState = state) {
            PlaylistDetailsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            PlaylistDetailsState.NotFound -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Плейлист не найден")
                }
            }
            is PlaylistDetailsState.Content -> {
                PlaylistDetailsContent(
                    content = currentState,
                    paddingValues = paddingValues,
                    onTrackClick = { track ->
                        navigateToPlayer(navController, track)
                    },
                    onTrackLongClick = { track ->
                        showDeleteTrackDialog = track
                    },
                    onShareClick = {
                        sharePlaylist(context, currentState)
                    },
                    onMenuClick = { showMenuSheet = true }
                )
            }
        }

        if (showMenuSheet && state is PlaylistDetailsState.Content) {
            val content = state as PlaylistDetailsState.Content
            ModalBottomSheet(
                onDismissRequest = { showMenuSheet = false },
                sheetState = sheetState
            ) {
                PlaylistMenuContent(
                    playlist = content.playlist,
                    onShareClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showMenuSheet = false
                            sharePlaylist(context, content)
                        }
                    },
                    onEditClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showMenuSheet = false
                            navController.navigate("${NavRoutes.EditPlaylist.route}/${content.playlist.id}")
                        }
                    },
                    onDeleteClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showMenuSheet = false
                            showDeleteDialog = true
                        }
                    }
                )
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Удалить плейлист") },
                text = { Text("Хотите удалить плейлист?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deletePlaylist()
                        }
                    ) {
                        Text("Да")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Нет")
                    }
                }
            )
        }

        showDeleteTrackDialog?.let { track ->
            AlertDialog(
                onDismissRequest = { showDeleteTrackDialog = null },
                title = { Text("Удалить трек") },
                text = { Text("Хотите удалить трек?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.removeTrack(track.trackId)
                            showDeleteTrackDialog = null
                        }
                    ) {
                        Text("Да")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteTrackDialog = null }) {
                        Text("Нет")
                    }
                }
            )
        }
    }
}

@Composable
private fun PlaylistDetailsContent(
    content: PlaylistDetailsState.Content,
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    onTrackClick: (Track) -> Unit,
    onTrackLongClick: (Track) -> Unit,
    onShareClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        item {
            AsyncImage(
                model = content.playlist.coverPath,
                contentDescription = content.playlist.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = content.playlist.name,
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                content.playlist.description?.let { desc ->
                    Text(
                        text = desc,
                        style = Typography.bodyMedium,
                        color = YpGray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Text(
                    text = "${formatDuration(content.totalDurationMillis)} • ${getTracksCountText(content.tracks.size)}",
                    style = Typography.bodySmall,
                    color = YpGray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Поделиться"
                        )
                    }
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Меню"
                        )
                    }
                }
            }
        }

        if (content.tracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PlaceholderError(type = PlaceholderType.EMPTY_PLAYLIST_TRACKS)
                }
            }
        } else {
            items(content.tracks) { track ->
                TrackItem(
                    track = track,
                    onClick = { onTrackClick(track) },
                    onLongClick = { onTrackLongClick(track) }
                )
            }
        }
    }
}

@Composable
private fun PlaylistMenuContent(
    playlist: Playlist,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = playlist.coverPath,
                contentDescription = playlist.name,
                modifier = Modifier
                    .width(45.dp)
                    .aspectRatio(1f)
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

        MenuItem(text = "Поделиться", onClick = onShareClick)
        MenuItem(text = "Редактировать информацию", onClick = onEditClick)
        MenuItem(text = "Удалить плейлист", onClick = onDeleteClick)
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun MenuItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = Typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp)
    )
}

private fun navigateToPlayer(navController: NavHostController, track: Track) {
    val trackJson = Gson().toJson(track)
    val encodedJson = URLEncoder.encode(trackJson, "UTF-8")
    navController.navigate("${NavRoutes.Player.route}/$encodedJson")
}

private fun sharePlaylist(context: Context, content: PlaylistDetailsState.Content) {
    if (content.tracks.isEmpty()) {
        Toast.makeText(
            context,
            "В этом плейлисте нет списка треков, которым можно поделиться",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    val shareText = buildString {
        appendLine(content.playlist.name)
        content.playlist.description?.let { appendLine(it) }
        appendLine(getTracksCountText(content.tracks.size))
        appendLine()
        content.tracks.forEachIndexed { index, track ->
            val minutes = track.trackTime / 60000
            val seconds = (track.trackTime % 60000) / 1000
            appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${String.format("%02d:%02d", minutes, seconds)})")
        }
    }

    Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }.let { sendIntent ->
        context.startActivity(Intent.createChooser(sendIntent, "Поделиться через..."))
    }
}

private fun formatDuration(millis: Long): String {
    val minutes = millis / 60000
    return "$minutes мин"
}

private fun getTracksCountText(count: Int): String {
    return when {
        count % 100 in 11..19 -> "$count треков"
        count % 10 == 1 -> "$count трек"
        count % 10 in 2..4 -> "$count трека"
        else -> "$count треков"
    }
}
