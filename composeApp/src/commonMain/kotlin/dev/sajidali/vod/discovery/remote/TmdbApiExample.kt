package dev.sajidali.vod.discovery.remote

import dev.sajidali.vod.discovery.remote.api.TmdbApi
import dev.sajidali.vod.discovery.remote.model.GenreListResponse
import dev.sajidali.vod.discovery.remote.model.MovieDetail
import dev.sajidali.vod.discovery.remote.model.MovieListResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Example usage of the TMDb API client
 * 
 * This is just an example and should not be used in production code.
 * In a real application, you would inject the API client and use it in your repositories or view models.
 */
object TmdbApiExample : KoinComponent {

    // Inject the TMDb API client from Koin
    private val tmdbApi: TmdbApi by inject()

    /**
     * Example of how to use the TMDb API client
     */
    fun runExample() {

        // Use a coroutine scope to make the API calls
        val scope = CoroutineScope(Dispatchers.Default)

        // Example of getting trending movies
        scope.launch {
            try {
                val trendingMovies = tmdbApi.getTrendingMovies()
                println("Trending Movies: ${trendingMovies.results.size}")

                // Print the first movie's title and poster URL
                if (trendingMovies.results.isNotEmpty()) {
                    val firstMovie = trendingMovies.results.first()
                    println("First Movie: ${firstMovie.title}")
                    println("Poster URL: ${tmdbApi.buildImageUrl(firstMovie.posterPath)}")
                }
            } catch (e: Exception) {
                println("Error getting trending movies: ${e.message}")
            }
        }

        // Example of getting top-rated movies
        scope.launch {
            try {
                val topRatedMovies = tmdbApi.getTopRatedMovies()
                println("Top Rated Movies: ${topRatedMovies.results.size}")
            } catch (e: Exception) {
                println("Error getting top-rated movies: ${e.message}")
            }
        }

        // Example of searching for movies
        scope.launch {
            try {
                val searchResults = tmdbApi.searchMovies("Avengers")
                println("Search Results: ${searchResults.results.size}")
            } catch (e: Exception) {
                println("Error searching for movies: ${e.message}")
            }
        }

        // Example of getting movie details
        scope.launch {
            try {
                // Using a known movie ID (The Avengers)
                val movieDetails = tmdbApi.getMovieDetails(24428)
                println("Movie Details: ${movieDetails.title}")
                println("Overview: ${movieDetails.overview}")
            } catch (e: Exception) {
                println("Error getting movie details: ${e.message}")
            }
        }

        // Example of getting genres
        scope.launch {
            try {
                val genres = tmdbApi.getGenres()
                println("Genres: ${genres.genres.size}")
                genres.genres.forEach { genre ->
                    println("Genre: ${genre.name} (${genre.id})")
                }
            } catch (e: Exception) {
                println("Error getting genres: ${e.message}")
            }
        }
    }
}
