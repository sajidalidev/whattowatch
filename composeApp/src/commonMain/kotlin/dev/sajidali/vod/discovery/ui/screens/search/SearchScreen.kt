package dev.sajidali.vod.discovery.ui.screens.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab as VoyagerTab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.sajidali.vod.discovery.remote.model.Movie
import dev.sajidali.vod.discovery.remote.model.TvShow
import dev.sajidali.vod.discovery.ui.components.MoviePoster
import dev.sajidali.vod.discovery.ui.components.TvShowPoster
import dev.sajidali.vod.discovery.ui.screens.moviedetails.MovieDetailsScreen
import dev.sajidali.vod.discovery.ui.screens.tvshows.TvShowDetailScreen
import org.koin.compose.koinInject

object SearchTab : VoyagerTab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 2u,
            title = "Search"
        )

    @Composable
    override fun Content() {
        // Use a nested Navigator within the Tab to handle Screen navigation
        Navigator(SearchScreenContent())
    }
}

// Define SearchScreenContent as a Screen to be used with the nested Navigator
class SearchScreenContent : Screen {
    @Composable
    override fun Content() {
        SearchScreen()
    }
}

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinInject()
) {
    // Get the navigator for screen navigation
    val navigator = LocalNavigator.currentOrThrow

    // Collect states from ViewModel
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val movieSearchState by viewModel.movieSearchState.collectAsState()
    val tvShowSearchState by viewModel.tvShowSearchState.collectAsState()

    // Create a column with max width for tablets/desktops
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Search input field with clear button
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text("Search Movies and TV Shows") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true
        )

        // Tabs for Movies and TV Shows
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { viewModel.setSelectedTab(0) },
                text = { Text("Movies") },
                icon = { Icon(Icons.Default.Home, contentDescription = null) }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { viewModel.setSelectedTab(1) },
                text = { Text("TV Shows") },
                icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show appropriate content based on selected tab
        when (selectedTab) {
            0 -> MovieSearchResults(
                state = movieSearchState,
                onMovieClick = { movie -> navigator.push(MovieDetailsScreen(movie.id)) },
                onLoadMore = viewModel::loadMoreMovies,
                onRetry = viewModel::retryMovieSearch
            )
            1 -> TvShowSearchResults(
                state = tvShowSearchState,
                onTvShowClick = { tvShow -> navigator.push(TvShowDetailScreen(tvShow.id)) },
                onLoadMore = viewModel::loadMoreTvShows,
                onRetry = viewModel::retryTvShowSearch
            )
        }
    }
}

@Composable
fun MovieSearchResults(
    state: SearchState<Movie>,
    onMovieClick: (Movie) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
) {
    when (state) {
        is SearchState.Initial -> {
            // Show initial state (no search performed yet)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Enter a search term to find movies",
                    textAlign = TextAlign.Center
                )
            }
        }

        is SearchState.Loading -> {
            // Show loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is SearchState.LoadingMore -> {
            // Show results with loading indicator at bottom
            SearchResultsList(
                items = state.data,
                onLoadMore = onLoadMore,
                isLoadingMore = true,
                itemContent = { movie ->
                    MovieSearchItem(movie = movie, onClick = onMovieClick)
                }
            )
        }

        is SearchState.Success -> {
            if (state.data.isEmpty()) {
                // Show empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No movies found",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Show results
                SearchResultsList(
                    items = state.data,
                    onLoadMore = onLoadMore,
                    isLoadingMore = false,
                    itemContent = { movie ->
                        MovieSearchItem(movie = movie, onClick = onMovieClick)
                    }
                )
            }
        }

        is SearchState.Error -> {
            // Show error state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
fun TvShowSearchResults(
    state: SearchState<TvShow>,
    onTvShowClick: (TvShow) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
) {
    when (state) {
        is SearchState.Initial -> {
            // Show initial state (no search performed yet)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Enter a search term to find TV shows",
                    textAlign = TextAlign.Center
                )
            }
        }

        is SearchState.Loading -> {
            // Show loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is SearchState.LoadingMore -> {
            // Show results with loading indicator at bottom
            SearchResultsList(
                items = state.data,
                onLoadMore = onLoadMore,
                isLoadingMore = true,
                itemContent = { tvShow ->
                    TvShowSearchItem(tvShow = tvShow, onClick = onTvShowClick)
                }
            )
        }

        is SearchState.Success -> {
            if (state.data.isEmpty()) {
                // Show empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No TV shows found",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Show results
                SearchResultsList(
                    items = state.data,
                    onLoadMore = onLoadMore,
                    isLoadingMore = false,
                    itemContent = { tvShow ->
                        TvShowSearchItem(tvShow = tvShow, onClick = onTvShowClick)
                    }
                )
            }
        }

        is SearchState.Error -> {
            // Show error state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
fun <T> SearchResultsList(
    items: List<T>,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean,
    itemContent: @Composable (T) -> Unit
) {
    val listState = rememberLazyListState()

    // Check if we should load more items
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= items.size - 5
        }
    }

    // Trigger load more when we're near the end of the list
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoadingMore) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            itemContent(item)
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
fun MovieSearchItem(
    movie: Movie,
    onClick: (Movie) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Movie poster
        MoviePoster(
            movie = movie,
            showTitle = false,
            onClick = onClick,
            modifier = Modifier.width(100.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Movie details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Title
            Text(
                text = movie.title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )

            // Release date
            if (movie.releaseDate != null) {
                Text(
                    text = "Released: ${movie.releaseDate}",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Rating
            if (movie.voteAverage != null) {
                Text(
                    text = "⭐ ${(movie.voteAverage * 10).toInt() / 10.0}",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TvShowSearchItem(
    tvShow: TvShow,
    onClick: (TvShow) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TV show poster
        TvShowPoster(
            tvShow = tvShow,
            showName = false,
            showRating = false,
            onClick = onClick,
            modifier = Modifier.width(100.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // TV show details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Name
            Text(
                text = tvShow.name,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )

            // First air date
            if (tvShow.firstAirDate != null) {
                Text(
                    text = "First aired: ${tvShow.firstAirDate}",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Rating
            if (tvShow.voteAverage != null) {
                Text(
                    text = "⭐ ${(tvShow.voteAverage * 10).toInt() / 10.0}",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
