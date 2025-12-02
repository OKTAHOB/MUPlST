package com.example.playlistmaker.features.media.presentation.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.playlistmaker.features.media.presentation.viewmodel.FavoritesState
import com.example.playlistmaker.features.media.presentation.viewmodel.FavoritesViewModel
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.ui.components.PlaceholderError
import com.example.playlistmaker.ui.components.PlaceholderType
import com.example.playlistmaker.ui.components.TrackItem
import com.example.playlistmaker.ui.navigation.NavRoutes
import com.google.gson.Gson
import org.koin.compose.viewmodel.koinViewModel
import java.net.URLEncoder

@Composable
fun FavoritesScreen(
    navController: NavHostController,
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val state by viewModel.state.observeAsState(FavoritesState.Empty)

    when (val currentState = state) {
        is FavoritesState.Content -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(currentState.tracks) { track ->
                    TrackItem(
                        track = track,
                        onClick = { navigateToPlayer(navController, track) }
                    )
                }
            }
        }
        FavoritesState.Empty -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                PlaceholderError(type = PlaceholderType.EMPTY_FAVORITES)
            }
        }
    }
}

private fun navigateToPlayer(navController: NavHostController, track: Track) {
    val trackJson = Gson().toJson(track)
    val encodedJson = URLEncoder.encode(trackJson, "UTF-8")
    navController.navigate("${NavRoutes.Player.route}/$encodedJson")
}
