package com.example.playlistmaker.features.media.presentation.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.features.media.domain.model.Playlist
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistState
import com.example.playlistmaker.features.media.presentation.viewmodel.PlaylistViewModel
import com.example.playlistmaker.ui.components.AppButton
import com.example.playlistmaker.ui.components.PlaceholderError
import com.example.playlistmaker.ui.components.PlaceholderType
import com.example.playlistmaker.ui.navigation.NavRoutes
import com.example.playlistmaker.ui.theme.Typography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PlaylistsScreen(
    navController: NavHostController,
    viewModel: PlaylistViewModel = koinViewModel()
) {
    val state by viewModel.state.observeAsState(PlaylistState.Empty)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppButton(
            text = "Создать плейлист",
            onClick = { navController.navigate(NavRoutes.CreatePlaylist.route) },
            modifier = Modifier.padding(top = 24.dp)
        )

        when (val currentState = state) {
            is PlaylistState.Content -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(currentState.playlists) { playlist ->
                        PlaylistItem(
                            playlist = playlist,
                            onClick = {
                                navController.navigate("${NavRoutes.PlaylistDetails.route}/${playlist.id}")
                            }
                        )
                    }
                }
            }
            PlaylistState.Empty -> {
                PlaceholderError(type = PlaceholderType.EMPTY_PLAYLISTS)
            }
        }
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = playlist.coverPath,
            contentDescription = "Обложка плейлиста",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.img_placeholder),
            error = painterResource(R.drawable.img_placeholder)
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = playlist.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = com.example.playlistmaker.ui.theme.PlaylistInfoStyle
        )
        Text(
            text = "${playlist.trackCount} треков",
            style = Typography.labelSmall
        )
    }
}
