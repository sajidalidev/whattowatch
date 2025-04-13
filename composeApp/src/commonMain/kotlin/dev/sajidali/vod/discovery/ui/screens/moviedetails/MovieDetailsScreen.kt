package dev.sajidali.vod.discovery.ui.screens.moviedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.SubcomposeAsyncImage
import dev.sajidali.vod.discovery.remote.api.TmdbApi
import dev.sajidali.vod.discovery.remote.model.MovieDetail
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

data class MovieDetailsScreen(val movieId: Int) : Screen {

    @Composable
    override fun Content() {
        val viewModel: MovieDetailsViewModel = koinInject { parametersOf(movieId) }
        val tmdbApi: TmdbApi = koinInject()
        val navigator = LocalNavigator.currentOrThrow

        val movieDetailState by viewModel.movieDetailState.collectAsState()
        val isInWatchlist by viewModel.isInWatchlist.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        when (movieDetailState) {
                            is MovieDetailState.Success -> Text(text = (movieDetailState as MovieDetailState.Success).movieDetail.title)
                            else -> Text(text = "Movie Details")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        // Watchlist button
                        if (movieDetailState is MovieDetailState.Success) {
                            IconButton(onClick = { viewModel.toggleWatchlist() }) {
                                Icon(
                                    imageVector = if (isInWatchlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (isInWatchlist) "Remove from watchlist" else "Add to watchlist",
                                    tint = if (isInWatchlist) Color.Red else Color.White
                                )
                            }
                        }
                    },
                    backgroundColor = MaterialTheme.colors.primary
                )
            }
        ) { paddingValues ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val maxWidth = this.maxWidth
                val isWideScreen = maxWidth > 600.dp

                when (movieDetailState) {
                    is MovieDetailState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is MovieDetailState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Error: ${(movieDetailState as MovieDetailState.Error).message}",
                                    style = MaterialTheme.typography.body1,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                                Button(onClick = { viewModel.loadMovieDetails() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    is MovieDetailState.Success -> {
                        val movie = (movieDetailState as MovieDetailState.Success).movieDetail

                        if (isWideScreen) {
                            // Tablet/Desktop layout (2-column)
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                // Left column - Poster
                                Box(
                                    modifier = Modifier
                                        .weight(0.4f)
                                        .fillMaxHeight()
                                ) {
                                    MovieDetailsPoster(
                                        posterPath = movie.posterPath,
                                        title = movie.title,
                                        tmdbApi = tmdbApi,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(2f/3f)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Right column - Details
                                Column(
                                    modifier = Modifier
                                        .weight(0.6f)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    MovieDetailsContent(movie = movie)
                                }
                            }
                        } else {
                            // Phone layout (stacked vertically)
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                // Banner at the top
                                MovieDetailsBanner(
                                    backdropPath = movie.backdropPath,
                                    posterPath = movie.posterPath,
                                    title = movie.title,
                                    tmdbApi = tmdbApi
                                )

                                // Content below
                                MovieDetailsContent(
                                    movie = movie,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieDetailsBanner(
    backdropPath: String?,
    posterPath: String?,
    title: String,
    tmdbApi: TmdbApi,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        // Use backdrop if available, otherwise use poster
        val imagePath = backdropPath ?: posterPath
        val imageUrl = tmdbApi.buildImageUrl(imagePath, size = "w780")

        if (imageUrl != null) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                error = {
                    // Fallback for error
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colors.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title.take(1),
                            style = MaterialTheme.typography.h4,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            )
        } else {
            // Fallback if no image path is available
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.take(1),
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }

        // Gradient overlay at the bottom for better text contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 150f
                    )
                )
        )
    }
}

@Composable
fun MovieDetailsPoster(
    posterPath: String?,
    title: String,
    tmdbApi: TmdbApi,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val imageUrl = tmdbApi.buildImageUrl(posterPath, size = "w500")

        if (imageUrl != null) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                error = {
                    // Fallback for error
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colors.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title.take(1),
                            style = MaterialTheme.typography.h4,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            )
        } else {
            // Fallback if no poster path is available
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.take(1),
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

@Composable
fun MovieDetailsContent(
    movie: MovieDetail,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Title and Release Year
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            movie.releaseDate?.let { releaseDate ->
                if (releaseDate.length >= 4) {
                    Text(
                        text = "(${releaseDate.substring(0, 4)})",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Rating
        movie.voteAverage?.let { rating ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "⭐ ${(rating * 10).toInt() / 10.0} / 10",
                    style = MaterialTheme.typography.body1
                )

                movie.runtime?.let { runtime ->
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "$runtime min",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Genres
        movie.genres?.let { genres ->
            if (genres.isNotEmpty()) {
                Text(
                    text = genres.joinToString(", ") { it.name },
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Overview
        movie.overview?.let { overview ->
            Text(
                text = "Overview",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = overview,
                style = MaterialTheme.typography.body1,
                lineHeight = MaterialTheme.typography.body1.fontSize * 1.5
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Additional details
        movie.originalLanguage?.let { language ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Language: ",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = language.uppercase(),
                    style = MaterialTheme.typography.body2
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
