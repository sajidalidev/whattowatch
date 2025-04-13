package dev.sajidali.vod.discovery.ui.screens.tvshows

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.SubcomposeAsyncImage
import dev.sajidali.vod.discovery.remote.api.TmdbApi
import dev.sajidali.vod.discovery.remote.model.Episode
import dev.sajidali.vod.discovery.remote.model.Genre
import dev.sajidali.vod.discovery.remote.model.TvShowDetail
import dev.sajidali.vod.discovery.remote.model.TvShowSeasonResponse
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

/**
 * TV Show Detail Screen
 * @param tvShowId The ID of the TV show to display
 */
class TvShowDetailScreen(private val tvShowId: Int) : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinInject<TvShowDetailViewModel> { parametersOf(tvShowId) }
        TvShowDetailContent(viewModel)
    }
}

@Composable
fun TvShowDetailContent(
    viewModel: TvShowDetailViewModel,
    tmdbApi: TmdbApi = koinInject()
) {
    val tvShowDetailState by viewModel.tvShowDetailState.collectAsState()
    val seasonsState by viewModel.seasonsState.collectAsState()
    val isInWatchlist by viewModel.isInWatchlist.collectAsState()

    when (val state = tvShowDetailState) {
        is TvShowDetailState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is TvShowDetailState.Success -> {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val maxWidth = this.maxWidth
                val isWideScreen = maxWidth > 600.dp

                if (isWideScreen) {
                    // Tablet/desktop layout
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Left column: Poster + basic info
                        Column(
                            modifier = Modifier
                                .weight(0.4f)
                                .padding(16.dp)
                        ) {
                            TvShowPosterBanner(
                                tvShowDetail = state.tvShowDetail,
                                tmdbApi = tmdbApi,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TvShowBasicInfo(
                                tvShowDetail = state.tvShowDetail,
                                isInWatchlist = isInWatchlist,
                                onToggleWatchlist = { viewModel.toggleWatchlist() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Right column: Overview + seasons
                        Column(
                            modifier = Modifier
                                .weight(0.6f)
                                .padding(16.dp)
                        ) {
                            TvShowOverview(
                                tvShowDetail = state.tvShowDetail,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TvShowSeasons(
                                seasonsState = seasonsState,
                                onSeasonClick = viewModel::toggleSeasonExpanded,
                                tmdbApi = tmdbApi,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    // Phone layout
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            TvShowPosterBanner(
                                tvShowDetail = state.tvShowDetail,
                                tmdbApi = tmdbApi,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                TvShowBasicInfo(
                                    tvShowDetail = state.tvShowDetail,
                                    isInWatchlist = isInWatchlist,
                                    onToggleWatchlist = { viewModel.toggleWatchlist() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                TvShowOverview(
                                    tvShowDetail = state.tvShowDetail,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                TvShowSeasons(
                                    seasonsState = seasonsState,
                                    onSeasonClick = viewModel::toggleSeasonExpanded,
                                    tmdbApi = tmdbApi,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
        is TvShowDetailState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material.Button(onClick = { viewModel.loadTvShowDetails() }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
fun TvShowPosterBanner(
    tvShowDetail: TvShowDetail,
    tmdbApi: TmdbApi,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(300.dp)
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
    ) {
        // Get the full image URL from the API using w780 size as specified in requirements
        val imageUrl = tmdbApi.getPosterUrl(tvShowDetail.posterPath, "w780")

        if (imageUrl != null) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = tvShowDetail.name,
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
                            text = tvShowDetail.name.take(1),
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
                    text = tvShowDetail.name.take(1),
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }

        // Gradient overlay for better readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 300f,
                        endY = 900f
                    )
                )
        )
    }
}

@Composable
fun TvShowBasicInfo(
    tvShowDetail: TvShowDetail,
    isInWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Title + First Air Date + Watchlist Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tvShowDetail.name,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold
                )
            }

            // Watchlist button
            IconButton(onClick = onToggleWatchlist) {
                Icon(
                    imageVector = if (isInWatchlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isInWatchlist) "Remove from watchlist" else "Add to watchlist",
                    tint = if (isInWatchlist) Color.Red else MaterialTheme.colors.onSurface
                )
            }

            // Extract year from firstAirDate
            tvShowDetail.firstAirDate?.let { date ->
                if (date.length >= 4) {
                    Text(
                        text = "(${date.substring(0, 4)})",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Rating
        tvShowDetail.voteAverage?.let { rating ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFC107), // Amber color for star
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${(rating * 10).toInt() / 10.0} / 10",
                    style = MaterialTheme.typography.body1
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Genres
        tvShowDetail.genres?.let { genres ->
            if (genres.isNotEmpty()) {
                GenreChips(genres = genres)
            }
        }
    }
}

@Composable
fun GenreChips(
    genres: List<Genre>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(genres) { genre ->
            Surface(
                color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = genre.name,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun TvShowOverview(
    tvShowDetail: TvShowDetail,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        tvShowDetail.overview?.let { overview ->
            Text(
                text = overview,
                style = MaterialTheme.typography.body1
            )
        } ?: Text(
            text = "No overview available",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun TvShowSeasons(
    seasonsState: Map<Int, SeasonState>,
    onSeasonClick: (Int) -> Unit,
    tmdbApi: TmdbApi,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Seasons",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (seasonsState.isEmpty()) {
            Text(
                text = "No seasons information available",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                seasonsState.entries.sortedBy { it.key }.forEach { (seasonNumber, state) ->
                    SeasonItem(
                        seasonNumber = seasonNumber,
                        seasonState = state,
                        onClick = { onSeasonClick(seasonNumber) },
                        tmdbApi = tmdbApi
                    )
                }
            }
        }
    }
}

@Composable
fun SeasonItem(
    seasonNumber: Int,
    seasonState: SeasonState,
    onClick: () -> Unit,
    tmdbApi: TmdbApi,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Season header
        Card(
            elevation = 2.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .padding(12.dp)
            ) {
                // Season poster thumbnail (if available)
                seasonState.seasonData?.let { seasonData ->
                    val imageUrl = tmdbApi.buildImageUrl(seasonData.posterPath, "w92")
                    if (imageUrl != null) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            SubcomposeAsyncImage(
                                model = imageUrl,
                                contentDescription = seasonData.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                loading = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }

                // Season info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = seasonState.seasonData?.name ?: "Season $seasonNumber",
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold
                    )

                    seasonState.seasonData?.let { seasonData ->
                        Row {
                            // Episode count
                            seasonData.episodeCount?.let { count ->
                                Text(
                                    text = "$count episodes",
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                )
                            }

                            // Air date
                            seasonData.airDate?.let { airDate ->
                                if (airDate.isNotEmpty()) {
                                    Text(
                                        text = " • $airDate",
                                        style = MaterialTheme.typography.caption,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Loading indicator or expand/collapse arrow
                if (seasonState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    val rotation by animateFloatAsState(
                        targetValue = if (seasonState.isExpanded) 180f else 0f
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (seasonState.isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
        }

        // Episodes list (expanded)
        AnimatedVisibility(visible = seasonState.isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            ) {
                if (seasonState.error != null) {
                    Text(
                        text = "Error loading episodes: ${seasonState.error}",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.error
                    )
                } else if (seasonState.seasonData?.episodes.isNullOrEmpty()) {
                    Text(
                        text = "No episodes available",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(seasonState.seasonData?.episodes ?: emptyList()) { episode ->
                            EpisodeItem(episode = episode, tmdbApi = tmdbApi)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EpisodeItem(
    episode: Episode,
    tmdbApi: TmdbApi,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .width(180.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Episode still image or number
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                if (episode.stillPath != null) {
                    val imageUrl = tmdbApi.buildImageUrl(episode.stillPath, "w185")
                    if (imageUrl != null) {
                        SubcomposeAsyncImage(
                            model = imageUrl,
                            contentDescription = episode.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        )
                    } else {
                        EpisodeNumberCircle(episode.episodeNumber)
                    }
                } else {
                    EpisodeNumberCircle(episode.episodeNumber)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Episode info
            Text(
                text = episode.name,
                style = MaterialTheme.typography.subtitle2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Runtime
            episode.runtime?.let { runtime ->
                Text(
                    text = "${runtime}min",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun EpisodeNumberCircle(episodeNumber: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .background(
                color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                shape = CircleShape
            )
    ) {
        Text(
            text = episodeNumber.toString(),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
    }
}
