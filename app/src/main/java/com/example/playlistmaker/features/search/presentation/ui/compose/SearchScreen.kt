package com.example.playlistmaker.features.search.presentation.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.playlistmaker.R
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.presentation.viewmodel.SearchState
import com.example.playlistmaker.features.search.presentation.viewmodel.SearchViewModel
import com.example.playlistmaker.ui.components.AppButton
import com.example.playlistmaker.ui.components.AppTopBar
import com.example.playlistmaker.ui.components.PlaceholderError
import com.example.playlistmaker.ui.components.PlaceholderType
import com.example.playlistmaker.ui.components.TrackItem
import com.example.playlistmaker.ui.navigation.NavRoutes
import com.example.playlistmaker.ui.theme.Typography
import com.example.playlistmaker.ui.theme.YpGray
import com.example.playlistmaker.ui.theme.customTextFieldColors
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.net.URLEncoder

private const val SEARCH_DEBOUNCE_DELAY = 3000L

@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: SearchViewModel = koinViewModel()
) {
    var inputText by remember { mutableStateOf("") }
    val searchState by viewModel.searchState.observeAsState(SearchState.Empty)

    val scope = rememberCoroutineScope()
    var searchJob: Job? by remember { mutableStateOf(null) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        if (inputText.isEmpty()) {
            viewModel.loadSearchHistory()
        }
    }

    Scaffold(
        topBar = { AppTopBar(showBackButton = false, title = "Поиск") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = customTextFieldColors(),
                textStyle = Typography.bodySmall,
                value = inputText,
                onValueChange = { newValue ->
                    inputText = newValue
                    if (newValue.isEmpty()) {
                        searchJob?.cancel()
                        viewModel.loadSearchHistory()
                    } else {
                        searchJob?.cancel()
                        searchJob = scope.launch {
                            delay(SEARCH_DEBOUNCE_DELAY)
                            viewModel.searchTracks(newValue)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        searchJob?.cancel()
                        viewModel.searchTracks(inputText)
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true,
                placeholder = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_search_light),
                            contentDescription = null,
                            tint = YpGray
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            text = "Поиск",
                            color = YpGray
                        )
                    }
                },
                trailingIcon = {
                    if (inputText.isNotEmpty()) {
                        Icon(
                            modifier = Modifier.clickable {
                                inputText = ""
                                searchJob?.cancel()
                                viewModel.loadSearchHistory()
                            },
                            painter = painterResource(R.drawable.ic_clear),
                            contentDescription = "Очистить",
                            tint = YpGray
                        )
                    }
                },
                shape = RoundedCornerShape(15.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                SearchStateContent(
                    state = searchState,
                    viewModel = viewModel,
                    navController = navController,
                    onRetry = {
                        viewModel.retrySearch()
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchStateContent(
    state: SearchState,
    viewModel: SearchViewModel,
    navController: NavHostController,
    onRetry: () -> Unit
) {
    when (state) {
        is SearchState.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 140.dp)
                    .width(44.dp)
            )
        }
        is SearchState.Success -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.tracks) { track ->
                    TrackItem(
                        track = track,
                        onClick = {
                            viewModel.addTrackToHistory(track)
                            navigateToPlayer(navController, track)
                        }
                    )
                }
            }
        }
        is SearchState.NoResults -> {
            PlaceholderError(type = PlaceholderType.NOTHING_FOUND)
        }
        is SearchState.Error -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlaceholderError(type = PlaceholderType.NO_CONNECTION)
                AppButton(
                    text = "Обновить",
                    onClick = onRetry,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }
        }
        is SearchState.ShowHistory -> {
            HistoryContent(
                history = state.history,
                viewModel = viewModel,
                navController = navController
            )
        }
        is SearchState.Empty -> {
            // Empty state, show nothing
        }
    }
}

@Composable
private fun HistoryContent(
    history: List<Track>,
    viewModel: SearchViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            text = "Вы искали",
            textAlign = TextAlign.Center,
            style = Typography.titleMedium
        )
        LazyColumn {
            items(history) { track ->
                TrackItem(
                    track = track,
                    onClick = {
                        viewModel.addTrackToHistory(track)
                        navigateToPlayer(navController, track)
                    }
                )
            }
        }
        AppButton(
            text = "Очистить историю",
            onClick = { viewModel.clearSearchHistory() },
            modifier = Modifier.padding(10.dp)
        )
    }
}

private fun navigateToPlayer(navController: NavHostController, track: Track) {
    val trackJson = Gson().toJson(track)
    val encodedJson = URLEncoder.encode(trackJson, "UTF-8")
    navController.navigate("${NavRoutes.Player.route}/$encodedJson")
}
