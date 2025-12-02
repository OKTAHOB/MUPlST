package com.example.playlistmaker.features.media.presentation.ui.compose

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.features.media.presentation.viewmodel.CreatePlaylistEvent
import com.example.playlistmaker.features.media.presentation.viewmodel.CreatePlaylistViewModel
import com.example.playlistmaker.ui.components.AppButton
import com.example.playlistmaker.ui.components.AppTopBar
import com.example.playlistmaker.ui.theme.Typography
import com.example.playlistmaker.ui.theme.YpGray
import com.example.playlistmaker.ui.theme.YpLightGray
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreatePlaylistScreen(
    navController: NavHostController,
    viewModel: CreatePlaylistViewModel = koinViewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var coverUri by remember { mutableStateOf<Uri?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            coverUri = uri
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreatePlaylistEvent.Success -> {
                    Toast.makeText(
                        context,
                        "Плейлист ${event.playlistName} создан",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                }
                CreatePlaylistEvent.Error -> {
                    Toast.makeText(
                        context,
                        "Не удалось создать плейлист",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                CreatePlaylistEvent.PlaylistNotFound -> {
                    Toast.makeText(
                        context,
                        "Плейлист не найден",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun handleBack() {
        val hasUnsavedChanges = name.isNotBlank() || description.isNotBlank() || coverUri != null
        if (hasUnsavedChanges) {
            showExitDialog = true
        } else {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                showBackButton = true,
                title = "Новый плейлист",
                onBackClick = { handleBack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(YpLightGray)
                    .clickable {
                        pickImageLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (coverUri != null) {
                    AsyncImage(
                        model = coverUri,
                        contentDescription = "Обложка плейлиста",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = R.drawable.background_for_adding_photo,
                        contentDescription = "Добавить фото",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = Typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                textStyle = Typography.bodyMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            AppButton(
                text = "Создать",
                onClick = {
                    viewModel.createPlaylist(name, description.ifBlank { null }, coverUri?.toString())
                },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )
        }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Завершить создание плейлиста?") },
                text = { Text("Все несохраненные данные будут потеряны") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExitDialog = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("Завершить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}
