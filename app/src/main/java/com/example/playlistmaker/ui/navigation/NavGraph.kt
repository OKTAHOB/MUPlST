package com.example.playlistmaker.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.playlistmaker.features.media.presentation.ui.compose.CreatePlaylistScreen
import com.example.playlistmaker.features.media.presentation.ui.compose.EditPlaylistScreen
import com.example.playlistmaker.features.media.presentation.ui.compose.MediaLibraryScreen
import com.example.playlistmaker.features.media.presentation.ui.compose.PlaylistDetailsScreen
import com.example.playlistmaker.features.player.presentation.ui.compose.PlayerScreen
import com.example.playlistmaker.features.search.presentation.ui.compose.SearchScreen
import com.example.playlistmaker.features.settings.presentation.ui.compose.SettingsScreen
import java.net.URLDecoder

@Composable
fun NavGraph(
    navController: NavHostController,
    onThemeChanged: (Boolean) -> Unit
) {
    Box {
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Search.route
        ) {
            composable(route = NavRoutes.Search.route) {
                SearchScreen(navController = navController)
            }

            composable(route = NavRoutes.Media.route) {
                MediaLibraryScreen(navController = navController)
            }

            composable(route = NavRoutes.Settings.route) {
                SettingsScreen(
                    onThemeChanged = onThemeChanged,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "${NavRoutes.Player.route}/{trackJson}",
                arguments = listOf(navArgument("trackJson") { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedJson = backStackEntry.arguments?.getString("trackJson") ?: ""
                val trackJson = URLDecoder.decode(encodedJson, "UTF-8")
                PlayerScreen(
                    trackJson = trackJson,
                    navController = navController
                )
            }

            composable(route = NavRoutes.CreatePlaylist.route) {
                CreatePlaylistScreen(navController = navController)
            }

            composable(
                route = "${NavRoutes.EditPlaylist.route}/{playlistId}",
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
                EditPlaylistScreen(
                    playlistId = playlistId,
                    navController = navController
                )
            }

            composable(
                route = "${NavRoutes.PlaylistDetails.route}/{playlistId}",
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
                PlaylistDetailsScreen(
                    playlistId = playlistId,
                    navController = navController
                )
            }
        }
    }
}
