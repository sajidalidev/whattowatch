package dev.sajidali.vod.discovery.ui.screens.movies

import dev.sajidali.vod.discovery.remote.api.TmdbApi
import dev.sajidali.vod.discovery.remote.model.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Movies screen
 */
class MoviesViewModel(
    private val tmdbApi: TmdbApi
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // State for trending movies
    private val _trendingMoviesState = MutableStateFlow<MoviesState>(MoviesState.Loading)
    val trendingMoviesState: StateFlow<MoviesState> = _trendingMoviesState.asStateFlow()
    
    // State for top rated movies
    private val _topRatedMoviesState = MutableStateFlow<MoviesState>(MoviesState.Loading)
    val topRatedMoviesState: StateFlow<MoviesState> = _topRatedMoviesState.asStateFlow()
    
    // State for upcoming movies
    private val _upcomingMoviesState = MutableStateFlow<MoviesState>(MoviesState.Loading)
    val upcomingMoviesState: StateFlow<MoviesState> = _upcomingMoviesState.asStateFlow()
    
    init {
        loadTrendingMovies()
        loadTopRatedMovies()
        loadUpcomingMovies()
    }
    
    /**
     * Load trending movies
     */
    fun loadTrendingMovies() {
        viewModelScope.launch {
            _trendingMoviesState.value = MoviesState.Loading
            try {
                val response = tmdbApi.getTrendingMovies()
                _trendingMoviesState.value = MoviesState.Success(response.results)
            } catch (e: Exception) {
                _trendingMoviesState.value = MoviesState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Load top rated movies
     */
    fun loadTopRatedMovies() {
        viewModelScope.launch {
            _topRatedMoviesState.value = MoviesState.Loading
            try {
                val response = tmdbApi.getTopRatedMovies()
                _topRatedMoviesState.value = MoviesState.Success(response.results)
            } catch (e: Exception) {
                _topRatedMoviesState.value = MoviesState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Load upcoming movies
     */
    fun loadUpcomingMovies() {
        viewModelScope.launch {
            _upcomingMoviesState.value = MoviesState.Loading
            try {
                val response = tmdbApi.getUpcomingMovies()
                _upcomingMoviesState.value = MoviesState.Success(response.results)
            } catch (e: Exception) {
                _upcomingMoviesState.value = MoviesState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

/**
 * State for movies
 */
sealed class MoviesState {
    /**
     * Loading state
     */
    object Loading : MoviesState()
    
    /**
     * Success state
     * @param movies The list of movies
     */
    data class Success(val movies: List<Movie>) : MoviesState()
    
    /**
     * Error state
     * @param message The error message
     */
    data class Error(val message: String) : MoviesState()
}