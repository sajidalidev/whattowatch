package dev.sajidali.vod.discovery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.sajidali.vod.discovery.remote.api.TmdbApi
import dev.sajidali.vod.discovery.remote.model.TvShow
import org.koin.compose.koinInject

/**
 * TV Show poster component
 * @param tvShow The TV show to display
 * @param showName Whether to show the TV show name
 * @param showRating Whether to show the TV show rating
 * @param onClick Callback when the poster is clicked
 * @param modifier Modifier for the component
 */
@Composable
fun TvShowPoster(
    tvShow: TvShow,
    showName: Boolean = true,
    showRating: Boolean = true,
    onClick: (TvShow) -> Unit,
    modifier: Modifier = Modifier,
    tmdbApi: TmdbApi = koinInject()
) {
    Card(
        modifier = modifier
            .width(150.dp)
            .clickable { onClick(tvShow) },
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            // Load TV show poster using Coil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(225.dp),
                contentAlignment = Alignment.Center
            ) {
                // Get the full image URL from the API
                val imageUrl = tmdbApi.buildImageUrl(tvShow.posterPath)

                if (imageUrl != null) {
                    // Use SubcomposeAsyncImage to handle loading and error states
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = tvShow.name,
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
                                    text = tvShow.name.take(1),
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
                            text = tvShow.name.take(1),
                            style = MaterialTheme.typography.h4,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }

            if (showName || showRating) {
                Spacer(modifier = Modifier.height(4.dp))

                if (showName) {
                    Text(
                        text = tvShow.name,
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                if (showRating && tvShow.voteAverage != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107), // Amber color for star
                            modifier = Modifier.width(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "⭐ ${(tvShow.voteAverage * 10).toInt() / 10.0}",
                            style = MaterialTheme.typography.caption,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
