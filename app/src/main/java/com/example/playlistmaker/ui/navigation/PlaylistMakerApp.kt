package com.example.playlistmaker.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.playlistmaker.features.settings.presentation.viewmodel.SettingsViewModel
import com.example.playlistmaker.ui.components.BottomNavigationBar
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import org.koin.compose.viewmodel.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlaylistMakerApp() {
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val isDarkTheme by settingsViewModel.themeSettings.observeAsState(false)
    var currentTheme by remember { mutableStateOf(isDarkTheme) }

    LaunchedEffect(isDarkTheme) {
        currentTheme = isDarkTheme
    }

    val navController = rememberNavController()

    PlaylistMakerTheme(darkTheme = currentTheme) {
        Surface {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination?.route

            val hideBottomNav = currentDestination?.let { route ->
                route.startsWith(NavRoutes.Player.route) ||
                route.startsWith(NavRoutes.CreatePlaylist.route) ||
                route.startsWith(NavRoutes.EditPlaylist.route) ||
                route.startsWith(NavRoutes.PlaylistDetails.route)
            } ?: false

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars),
                bottomBar = {
                    if (!hideBottomNav) {
                        BottomNavigationBar(navController)
                    }
                }
            ) {
                NavGraph(
                    navController = navController,
                    onThemeChanged = { newTheme ->
                        currentTheme = newTheme
                    }
                )
            }
        }
    }
}
