package com.example.playlistmaker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavRoutes(
    val name: String,
    val route: String,
    val showBackButton: Boolean,
    val icon: ImageVector
) {
    data object Search : NavRoutes(
        name = "Поиск",
        route = "search",
        showBackButton = false,
        icon = Icons.Default.Search
    )

    data object Media : NavRoutes(
        name = "Медиатека",
        route = "media",
        showBackButton = false,
        icon = Icons.Default.PlayArrow
    )

    data object Settings : NavRoutes(
        name = "Настройки",
        route = "settings",
        showBackButton = false,
        icon = Icons.Default.Settings
    )

    data object Player : NavRoutes(
        name = "",
        route = "player",
        showBackButton = true,
        icon = Icons.AutoMirrored.Filled.ArrowBack
    )

    data object CreatePlaylist : NavRoutes(
        name = "Новый плейлист",
        route = "create_playlist",
        showBackButton = true,
        icon = Icons.AutoMirrored.Filled.ArrowBack
    )

    data object EditPlaylist : NavRoutes(
        name = "Редактировать",
        route = "edit_playlist",
        showBackButton = true,
        icon = Icons.AutoMirrored.Filled.ArrowBack
    )

    data object PlaylistDetails : NavRoutes(
        name = "",
        route = "playlist_details",
        showBackButton = true,
        icon = Icons.AutoMirrored.Filled.ArrowBack
    )

    companion object {
        val bottomMenuItems by lazy { listOf(Search, Media, Settings) }
    }
}
