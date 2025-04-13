package dev.sajidali.vod.discovery.ui.screens.tvshows

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.sajidali.vod.discovery.remote.model.TvShow
import dev.sajidali.vod.discovery.ui.components.TvShowPoster
import org.koin.compose.koinInject

object TVShowsTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 1u,
            title = "TV Shows"
        )

    @Composable
    override fun Content() {
        // Use a nested Navigator within the Tab to handle Screen navigation
        Navigator(TvShowsScreenContent())
    }
}

// Define TvShowsScreenContent as a Screen to be used with the nested Navigator
class TvShowsScreenContent : Screen {
    @Composable
    override fun Content() {
        TvShowsScreen()
    }
}

@Composable
fun TvShowsScreen(
    viewModel: TvShowsViewModel = koinInject()
) {
    // Get the current navigator
    val navigator = LocalNavigator.currentOrThrow

    // Collect states from ViewModel
    val popularTvShowsState by viewModel.popularTvShowsState.collectAsState()
    val topRatedTvShowsState by viewModel.topRatedTvShowsState.collectAsState()
    val airingTodayTvShowsState by viewModel.airingTodayTvShowsState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            TvShowSection(
                title = "Popular TV Shows",
                state = popularTvShowsState,
                onRetry = viewModel::loadPopularTvShows,
                onTvShowClick = { tvShow ->
                    // Navigate to TV show details
                    navigator.push(TvShowDetailScreen(tvShow.id))
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            TvShowSection(
                title = "Top Rated TV Shows",
                state = topRatedTvShowsState,
                onRetry = viewModel::loadTopRatedTvShows,
                onTvShowClick = { tvShow ->
                    // Navigate to TV show details
                    navigator.push(TvShowDetailScreen(tvShow.id))
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            TvShowSection(
                title = "Airing Today",
                state = airingTodayTvShowsState,
                onRetry = viewModel::loadAiringTodayTvShows,
                onTvShowClick = { tvShow ->
                    // Navigate to TV show details
                    navigator.push(TvShowDetailScreen(tvShow.id))
                }
            )
        }
    }
}

@Composable
fun TvShowSection(
    title: String,
    state: TvShowsState,
    onRetry: () -> Unit,
    onTvShowClick: (TvShow) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        when (state) {
            is TvShowsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TvShowsState.Success -> {
                if (state.tvShows.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No TV shows found",
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.tvShows) { tvShow ->
                            TvShowPoster(
                                tvShow = tvShow,
                                showName = true,
                                showRating = true,
                                onClick = onTvShowClick,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }
            }
            is TvShowsState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: ${state.message}",
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        androidx.compose.material.Button(onClick = onRetry) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}
