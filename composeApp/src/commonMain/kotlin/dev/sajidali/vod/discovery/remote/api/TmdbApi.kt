package dev.sajidali.vod.discovery.remote.api

import dev.sajidali.vod.discovery.remote.model.GenreListResponse
import dev.sajidali.vod.discovery.remote.model.MovieDetail
import dev.sajidali.vod.discovery.remote.model.MovieListResponse
import dev.sajidali.vod.discovery.remote.model.TvShowDetail
import dev.sajidali.vod.discovery.remote.model.TvShowListResponse
import dev.sajidali.vod.discovery.remote.model.TvShowSeasonResponse

/**
 * Interface for the TMDb API client
 */
interface TmdbApi {
    /**
     * Get trending movies
     * @param timeWindow The time window for trending movies. Can be "day" or "week"
     * @param page The page number to fetch
     * @return A [MovieListResponse] containing the trending movies
     */
    suspend fun getTrendingMovies(timeWindow: String = "week", page: Int = 1): MovieListResponse

    /**
     * Get top-rated movies
     * @param page The page number to fetch
     * @return A [MovieListResponse] containing the top-rated movies
     */
    suspend fun getTopRatedMovies(page: Int = 1): MovieListResponse

    /**
     * Get upcoming movies
     * @param page The page number to fetch
     * @return A [MovieListResponse] containing the upcoming movies
     */
    suspend fun getUpcomingMovies(page: Int = 1): MovieListResponse

    /**
     * Search for movies by query
     * @param query The search query
     * @param page The page number to fetch
     * @return A [MovieListResponse] containing the search results
     */
    suspend fun searchMovies(query: String, page: Int = 1): MovieListResponse

    /**
     * Get movie details by ID
     * @param movieId The ID of the movie to fetch
     * @return A [MovieDetail] containing the movie details
     */
    suspend fun getMovieDetails(movieId: Int): MovieDetail

    /**
     * Get list of movie genres
     * @return A [GenreListResponse] containing the list of movie genres
     */
    suspend fun getGenres(): GenreListResponse

    /**
     * Get popular TV shows
     * @param page The page number to fetch
     * @return A [TvShowListResponse] containing the popular TV shows
     */
    suspend fun getPopularTvShows(page: Int = 1): TvShowListResponse

    /**
     * Get top-rated TV shows
     * @param page The page number to fetch
     * @return A [TvShowListResponse] containing the top-rated TV shows
     */
    suspend fun getTopRatedTvShows(page: Int = 1): TvShowListResponse

    /**
     * Get TV shows airing today
     * @param page The page number to fetch
     * @return A [TvShowListResponse] containing the TV shows airing today
     */
    suspend fun getAiringTodayTvShows(page: Int = 1): TvShowListResponse

    /**
     * Search for TV shows by query
     * @param query The search query
     * @param page The page number to fetch
     * @return A [TvShowListResponse] containing the search results
     */
    suspend fun searchTvShows(query: String, page: Int = 1): TvShowListResponse

    /**
     * Get TV show details by ID
     * @param tvShowId The ID of the TV show to fetch
     * @return A [TvShowDetail] containing the TV show details
     */
    suspend fun getTvShowDetails(tvShowId: Int): TvShowDetail

    /**
     * Get list of TV show genres
     * @return A [GenreListResponse] containing the list of TV show genres
     */
    suspend fun getTvShowGenres(): GenreListResponse

    /**
     * Build an image URL for a poster or backdrop
     * @param path The path to the image (e.g., posterPath or backdropPath)
     * @param size The size of the image. Default is "w500" for posters
     * @return The full URL to the image
     */
    fun buildImageUrl(path: String?, size: String = "w500"): String?

    /**
     * Get TV show season details by TV show ID and season number
     * @param tvShowId The ID of the TV show
     * @param seasonNumber The season number
     * @return A [TvShowSeasonResponse] containing the season details and episodes
     */
    suspend fun getTvShowSeasonDetails(tvShowId: Int, seasonNumber: Int): TvShowSeasonResponse

    /**
     * Get poster URL for a TV show or movie
     * @param posterPath The poster path
     * @param size The size of the image. Default is "w500"
     * @return The full URL to the poster image
     */
    fun getPosterUrl(posterPath: String?, size: String = "w500"): String? = buildImageUrl(posterPath, size)
}
