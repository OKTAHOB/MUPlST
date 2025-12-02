package com.example.playlistmaker.features.settings.presentation.ui.compose

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.features.settings.presentation.viewmodel.SettingsViewModel
import com.example.playlistmaker.ui.components.AppTopBar
import com.example.playlistmaker.ui.theme.Typography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onThemeChanged: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val isDarkTheme by viewModel.themeSettings.observeAsState(false)
    val context = LocalContext.current

    Scaffold(
        topBar = { AppTopBar(showBackButton = false, title = "Настройки") { onBackClick() } }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            ThemeSwitcherRow(
                isDarkTheme = isDarkTheme,
                onThemeChange = { newValue ->
                    viewModel.updateThemeSettings(newValue)
                    onThemeChanged(newValue)
                }
            )
            SettingsRow(
                text = "Поделиться приложением",
                icon = painterResource(R.drawable.ic_share),
                onClick = { shareApp(context) }
            )
            SettingsRow(
                text = "Написать в поддержку",
                icon = painterResource(R.drawable.ic_support),
                onClick = { openSupport(context) }
            )
            SettingsRow(
                text = "Пользовательское соглашение",
                icon = painterResource(R.drawable.ic_next),
                onClick = { openTerms(context) }
            )
        }
    }
}

private val rowModifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 16.dp)
    .height(61.dp)

@Composable
private fun SettingsRow(
    text: String,
    icon: Painter,
    onClick: () -> Unit
) {
    Row(
        modifier = rowModifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(5.dp),
            style = Typography.bodyLarge
        )
        Icon(
            painter = icon,
            contentDescription = text,
            modifier = Modifier.fillMaxHeight()
        )
    }
}

@Composable
private fun ThemeSwitcherRow(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Row(
        modifier = rowModifier.clickable { onThemeChange(!isDarkTheme) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Темная тема",
            modifier = Modifier.padding(5.dp),
            style = Typography.bodyLarge
        )
        Switch(
            modifier = Modifier.scale(0.8f),
            checked = isDarkTheme,
            onCheckedChange = onThemeChange
        )
    }
}

private fun shareApp(context: Context) {
    val shareText = context.getString(R.string.course_url)
    Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }.let { sendIntent ->
        context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_via)))
    }
}

private fun openSupport(context: Context) {
    Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.sup_mail)))
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.message_to_sup_h))
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.message_to_sup))
    }.let { intent ->
        context.startActivity(intent)
    }
}

private fun openTerms(context: Context) {
    context.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.terms_url)))
    )
}
