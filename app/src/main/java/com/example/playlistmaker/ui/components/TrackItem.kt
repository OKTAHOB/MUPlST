package com.example.playlistmaker.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackItem(
    track: Track,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(48.dp)
                .padding(5.dp),
            model = track.artworkUrl100,
            contentDescription = null,
            placeholder = painterResource(R.drawable.img_placeholder)
        )
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f)
        ) {
            Text(
                text = track.trackName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.artistName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Typography.labelSmall
                )
                Icon(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    painter = painterResource(R.drawable.ic_tracks_divider),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = formatTrackTime(track.trackTime),
                    style = Typography.labelSmall
                )
            }
        }
        Icon(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(20.dp),
            painter = painterResource(R.drawable.ic_next),
            contentDescription = null
        )
    }
}

fun formatTrackTime(millis: Long): String {
    val format = SimpleDateFormat("mm:ss", Locale.getDefault())
    return format.format(millis)
}
