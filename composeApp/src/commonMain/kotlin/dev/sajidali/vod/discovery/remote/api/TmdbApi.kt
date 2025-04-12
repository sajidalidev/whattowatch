package dev.sajidali.vod.discovery.remote.api

import dev.sajidali.vod.discovery.remote.model.GenreListResponse
import dev.sajidali.vod.discovery.remote.model.MovieDetail
import dev.sajidali.vod.discovery.remote.model.MovieListResponse

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
     * Get list of genres
     * @return A [GenreListResponse] containing the list of genres
     */
    suspend fun getGenres(): GenreListResponse
    
    /**
     * Build an image URL for a poster or backdrop
     * @param path The path to the image (e.g., posterPath or backdropPath)
     * @param size The size of the image. Default is "w500" for posters
     * @return The full URL to the image
     */
    fun buildImageUrl(path: String?, size: String = "w500"): String?
}