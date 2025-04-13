package dev.sajidali.vod.discovery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import dev.sajidali.vod.discovery.remote.api.TmdbApi
import dev.sajidali.vod.discovery.remote.model.Movie
import org.koin.compose.koinInject

/**
 * Movie poster component
 * @param movie The movie to display
 * @param showTitle Whether to show the movie title
 * @param onClick Callback when the poster is clicked
 * @param modifier Modifier for the component
 */
@Composable
fun MoviePoster(
    movie: Movie,
    showTitle: Boolean = true,
    onClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    tmdbApi: TmdbApi = koinInject()
) {
    Card(
        modifier = modifier
            .width(150.dp)
            .clickable { onClick(movie) },
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            // Load movie poster using Coil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(225.dp),
                contentAlignment = Alignment.Center
            ) {
                // Get the full image URL from the API
                val imageUrl = tmdbApi.buildImageUrl(movie.posterPath)

                if (imageUrl != null) {
                    // Use SubcomposeAsyncImage to handle loading and error states
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = movie.title,
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
                            println(it.result.throwable)
                            // Fallback for error
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colors.primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = movie.title.take(1),
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
                            text = movie.title.take(1),
                            style = MaterialTheme.typography.h4,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }

            if (showTitle) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
