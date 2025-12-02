package com.example.playlistmaker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.theme.DarkBackground

enum class PlaceholderType {
    NOTHING_FOUND,
    NO_CONNECTION,
    EMPTY_FAVORITES,
    EMPTY_PLAYLISTS,
    EMPTY_PLAYLIST_TRACKS
}

@Composable
fun PlaceholderError(
    type: PlaceholderType,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground

    val (text, imageRes) = when (type) {
        PlaceholderType.NOTHING_FOUND -> "По вашему запросу ничего не найдено" to
                if (isDark) R.drawable.img_nothing_found_dark else R.drawable.img_nothing_found_light
        PlaceholderType.NO_CONNECTION -> "Нет подключения к интернету" to
                if (isDark) R.drawable.img_connection_problem_dark else R.drawable.img_connection_problem_light
        PlaceholderType.EMPTY_FAVORITES -> "Ваша медиатека пуста" to
                if (isDark) R.drawable.img_nothing_found_dark else R.drawable.img_nothing_found_light
        PlaceholderType.EMPTY_PLAYLISTS -> "Вы не создали ни одного плейлиста" to
                if (isDark) R.drawable.img_nothing_found_dark else R.drawable.img_nothing_found_light
        PlaceholderType.EMPTY_PLAYLIST_TRACKS -> "В плейлисте отсутствуют треки" to
                if (isDark) R.drawable.img_nothing_found_dark else R.drawable.img_nothing_found_light
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null
        )
        Text(text = text)
    }
}
