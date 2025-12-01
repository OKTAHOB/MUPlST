package com.example.playlistmaker.ui.components

import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.playlistmaker.ui.theme.customButtonColors

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick,
        colors = customButtonColors(),
        enabled = enabled
    ) {
        Text(text)
    }
}
