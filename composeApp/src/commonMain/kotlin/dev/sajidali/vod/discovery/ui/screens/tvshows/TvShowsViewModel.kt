package dev.sajidali.vod.discovery.ui.screens.tvshows

import dev.sajidali.vod.discovery.remote.api.TmdbApi
import dev.sajidali.vod.discovery.remote.model.TvShow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the TV Shows screen
 */
class TvShowsViewModel(
    private val tmdbApi: TmdbApi
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // State for popular TV shows
    private val _popularTvShowsState = MutableStateFlow<TvShowsState>(TvShowsState.Loading)
    val popularTvShowsState: StateFlow<TvShowsState> = _popularTvShowsState.asStateFlow()
    
    // State for top rated TV shows
    private val _topRatedTvShowsState = MutableStateFlow<TvShowsState>(TvShowsState.Loading)
    val topRatedTvShowsState: StateFlow<TvShowsState> = _topRatedTvShowsState.asStateFlow()
    
    // State for TV shows airing today
    private val _airingTodayTvShowsState = MutableStateFlow<TvShowsState>(TvShowsState.Loading)
    val airingTodayTvShowsState: StateFlow<TvShowsState> = _airingTodayTvShowsState.asStateFlow()
    
    init {
        loadPopularTvShows()
        loadTopRatedTvShows()
        loadAiringTodayTvShows()
    }
    
    /**
     * Load popular TV shows
     */
    fun loadPopularTvShows() {
        viewModelScope.launch {
            _popularTvShowsState.value = TvShowsState.Loading
            try {
                val response = tmdbApi.getPopularTvShows()
                _popularTvShowsState.value = TvShowsState.Success(response.results)
            } catch (e: Exception) {
                _popularTvShowsState.value = TvShowsState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Load top rated TV shows
     */
    fun loadTopRatedTvShows() {
        viewModelScope.launch {
            _topRatedTvShowsState.value = TvShowsState.Loading
            try {
                val response = tmdbApi.getTopRatedTvShows()
                _topRatedTvShowsState.value = TvShowsState.Success(response.results)
            } catch (e: Exception) {
                _topRatedTvShowsState.value = TvShowsState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Load TV shows airing today
     */
    fun loadAiringTodayTvShows() {
        viewModelScope.launch {
            _airingTodayTvShowsState.value = TvShowsState.Loading
            try {
                val response = tmdbApi.getAiringTodayTvShows()
                _airingTodayTvShowsState.value = TvShowsState.Success(response.results)
            } catch (e: Exception) {
                _airingTodayTvShowsState.value = TvShowsState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Load more popular TV shows
     * @param page The page number to fetch
     */
    fun loadMorePopularTvShows(page: Int) {
        viewModelScope.launch {
            try {
                val currentState = _popularTvShowsState.value
                if (currentState is TvShowsState.Success) {
                    val response = tmdbApi.getPopularTvShows(page)
                    _popularTvShowsState.value = TvShowsState.Success(
                        currentState.tvShows + response.results
                    )
                }
            } catch (e: Exception) {
                // Keep the current data but show an error message
                // This could be improved with a dedicated "LoadMoreError" state
            }
        }
    }
    
    /**
     * Load more top rated TV shows
     * @param page The page number to fetch
     */
    fun loadMoreTopRatedTvShows(page: Int) {
        viewModelScope.launch {
            try {
                val currentState = _topRatedTvShowsState.value
                if (currentState is TvShowsState.Success) {
                    val response = tmdbApi.getTopRatedTvShows(page)
                    _topRatedTvShowsState.value = TvShowsState.Success(
                        currentState.tvShows + response.results
                    )
                }
            } catch (e: Exception) {
                // Keep the current data but show an error message
            }
        }
    }
    
    /**
     * Load more TV shows airing today
     * @param page The page number to fetch
     */
    fun loadMoreAiringTodayTvShows(page: Int) {
        viewModelScope.launch {
            try {
                val currentState = _airingTodayTvShowsState.value
                if (currentState is TvShowsState.Success) {
                    val response = tmdbApi.getAiringTodayTvShows(page)
                    _airingTodayTvShowsState.value = TvShowsState.Success(
                        currentState.tvShows + response.results
                    )
                }
            } catch (e: Exception) {
                // Keep the current data but show an error message
            }
        }
    }
}

/**
 * State for TV shows
 */
sealed class TvShowsState {
    /**
     * Loading state
     */
    object Loading : TvShowsState()
    
    /**
     * Success state
     * @param tvShows The list of TV shows
     */
    data class Success(val tvShows: List<TvShow>) : TvShowsState()
    
    /**
     * Error state
     * @param message The error message
     */
    data class Error(val message: String) : TvShowsState()
}