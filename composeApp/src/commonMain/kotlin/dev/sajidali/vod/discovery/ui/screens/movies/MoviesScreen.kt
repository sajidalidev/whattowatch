package dev.sajidali.vod.discovery.ui.screens.movies

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
import androidx.compose.material.Scaffold
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
import dev.sajidali.vod.discovery.remote.model.Movie
import dev.sajidali.vod.discovery.ui.components.MoviePoster
import dev.sajidali.vod.discovery.ui.screens.moviedetails.MovieDetailsScreen
import org.koin.compose.koinInject

object MoviesTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 0u,
            title = "Movies"
        )

    @Composable
    override fun Content() {
        // Use a nested Navigator within the Tab to handle Screen navigation
        Navigator(MoviesScreen())
    }
}

// Define MoviesScreen as a Screen to be used with the nested Navigator
class MoviesScreen : Screen {
    @Composable
    override fun Content() {
        MoviesScreenContent()
    }
}

@Composable
fun MoviesScreenContent(
    viewModel: MoviesViewModel = koinInject()
) {
    // Get the navigator for screen navigation
    // This will now be the Navigator from the nested Navigator, not the TabNavigator
    val navigator = LocalNavigator.currentOrThrow

    // Collect states from ViewModel
    val trendingMoviesState by viewModel.trendingMoviesState.collectAsState()
    val topRatedMoviesState by viewModel.topRatedMoviesState.collectAsState()
    val upcomingMoviesState by viewModel.upcomingMoviesState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            MovieSection(
                title = "Trending Movies",
                state = trendingMoviesState,
                onRetry = viewModel::loadTrendingMovies,
                onMovieClick = { movie ->
                    // Navigate to movie details screen
                    navigator.push(MovieDetailsScreen(movie.id))
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            MovieSection(
                title = "Top Rated Movies",
                state = topRatedMoviesState,
                onRetry = viewModel::loadTopRatedMovies,
                onMovieClick = { movie ->
                    // Navigate to movie details screen
                    navigator.push(MovieDetailsScreen(movie.id))
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            MovieSection(
                title = "Upcoming Movies",
                state = upcomingMoviesState,
                onRetry = viewModel::loadUpcomingMovies,
                onMovieClick = { movie ->
                    // Navigate to movie details screen
                    navigator.push(MovieDetailsScreen(movie.id))
                }
            )
        }
    }
}

@Composable
fun MovieSection(
    title: String,
    state: MoviesState,
    onRetry: () -> Unit,
    onMovieClick: (Movie) -> Unit,
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
            is MoviesState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MoviesState.Success -> {
                if (state.movies.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No movies found",
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.movies) { movie ->
                            MoviePoster(
                                movie = movie,
                                showTitle = true,
                                onClick = onMovieClick,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }
            }
            is MoviesState.Error -> {
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
