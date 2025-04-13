package dev.sajidali.vod.discovery.ui.screens.moviedetails

import dev.sajidali.vod.discovery.data.model.toWatchlistItem
import dev.sajidali.vod.discovery.data.repository.WatchlistRepository
import dev.sajidali.vod.discovery.remote.api.TmdbApi
import dev.sajidali.vod.discovery.remote.model.MovieDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the movie details screen
 */
class MovieDetailsViewModel(
    private val tmdbApi: TmdbApi,
    private val watchlistRepository: WatchlistRepository,
    private val movieId: Int
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    
    // State for movie details
    private val _movieDetailState = MutableStateFlow<MovieDetailState>(MovieDetailState.Loading)
    val movieDetailState: StateFlow<MovieDetailState> = _movieDetailState.asStateFlow()
    
    // State for watchlist
    private val _isInWatchlist = MutableStateFlow(false)
    val isInWatchlist: StateFlow<Boolean> = _isInWatchlist.asStateFlow()
    
    init {
        loadMovieDetails()
        checkIfInWatchlist()
    }
    
    /**
     * Load movie details
     */
    fun loadMovieDetails() {
        coroutineScope.launch {
            _movieDetailState.value = MovieDetailState.Loading
            
            try {
                val movieDetail = tmdbApi.getMovieDetails(movieId)
                _movieDetailState.value = MovieDetailState.Success(movieDetail)
            } catch (e: Exception) {
                _movieDetailState.value = MovieDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Check if the movie is in the watchlist
     */
    private fun checkIfInWatchlist() {
        coroutineScope.launch {
            _isInWatchlist.value = watchlistRepository.isInWatchlist(movieId)
        }
    }
    
    /**
     * Toggle the movie's watchlist status
     */
    fun toggleWatchlist() {
        coroutineScope.launch {
            val currentState = _movieDetailState.value
            if (currentState is MovieDetailState.Success) {
                val movie = currentState.movieDetail
                
                if (_isInWatchlist.value) {
                    // Remove from watchlist
                    watchlistRepository.removeItem(movieId)
                } else {
                    // Add to watchlist
                    val watchlistItem = movie.toWatchlistItem()
                    watchlistRepository.addItem(watchlistItem)
                }
                
                // Update state
                _isInWatchlist.value = !_isInWatchlist.value
            }
        }
    }
}

/**
 * Extension function to convert a MovieDetail to a WatchlistItem
 */
fun MovieDetail.toWatchlistItem() = dev.sajidali.vod.discovery.data.model.WatchlistItem(
    id = id,
    title = title,
    posterPath = posterPath,
    overview = overview,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    itemType = dev.sajidali.vod.discovery.data.model.ItemType.MOVIE
)

/**
 * State for movie details
 */
sealed class MovieDetailState {
    /**
     * Loading state
     */
    object Loading : MovieDetailState()
    
    /**
     * Success state
     * @param movieDetail The movie details
     */
    data class Success(val movieDetail: MovieDetail) : MovieDetailState()
    
    /**
     * Error state
     * @param message The error message
     */
    data class Error(val message: String) : MovieDetailState()
}