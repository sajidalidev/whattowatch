package dev.sajidali.vod.discovery.ui.screens.search

import dev.sajidali.vod.discovery.remote.api.TmdbApi
import dev.sajidali.vod.discovery.remote.model.Movie
import dev.sajidali.vod.discovery.remote.model.TvShow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Search screen
 */
class SearchViewModel(
    private val tmdbApi: TmdbApi
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // State for movie search results
    private val _movieSearchState = MutableStateFlow<SearchState<Movie>>(SearchState.Initial)
    val movieSearchState: StateFlow<SearchState<Movie>> = _movieSearchState.asStateFlow()
    
    // State for TV show search results
    private val _tvShowSearchState = MutableStateFlow<SearchState<TvShow>>(SearchState.Initial)
    val tvShowSearchState: StateFlow<SearchState<TvShow>> = _tvShowSearchState.asStateFlow()
    
    // Current page for movie search
    private var currentMoviePage = 1
    private var totalMoviePages = 1
    
    // Current page for TV show search
    private var currentTvShowPage = 1
    private var totalTvShowPages = 1
    
    // Selected tab (0 for Movies, 1 for TV Shows)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()
    
    /**
     * Set the selected tab
     * @param tab The tab index (0 for Movies, 1 for TV Shows)
     */
    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }
    
    /**
     * Update the search query with debounce
     * @param query The search query
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        
        // Reset pagination
        currentMoviePage = 1
        currentTvShowPage = 1
        
        // If query is empty, reset search states
        if (query.isEmpty()) {
            _movieSearchState.value = SearchState.Initial
            _tvShowSearchState.value = SearchState.Initial
            return
        }
        
        // Debounce search
        viewModelScope.launch {
            delay(300) // 300ms debounce
            if (query == _searchQuery.value) {
                searchMovies(query)
                searchTvShows(query)
            }
        }
    }
    
    /**
     * Search for movies
     * @param query The search query
     * @param page The page number
     */
    private fun searchMovies(query: String, page: Int = 1) {
        viewModelScope.launch {
            // If it's the first page, show loading state
            if (page == 1) {
                _movieSearchState.value = SearchState.Loading
            } else {
                // For pagination, we keep the current results and show loading more
                _movieSearchState.value = SearchState.LoadingMore(
                    (_movieSearchState.value as? SearchState.Success)?.data ?: emptyList()
                )
            }
            
            try {
                val response = tmdbApi.searchMovies(query, page)
                totalMoviePages = response.totalPages
                
                // If it's the first page, replace the results
                if (page == 1) {
                    _movieSearchState.value = SearchState.Success(response.results)
                } else {
                    // For pagination, append the new results to the existing ones
                    val currentResults = (_movieSearchState.value as? SearchState.Success)?.data ?: emptyList()
                    _movieSearchState.value = SearchState.Success(currentResults + response.results)
                }
                
                currentMoviePage = page
            } catch (e: Exception) {
                _movieSearchState.value = SearchState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Search for TV shows
     * @param query The search query
     * @param page The page number
     */
    private fun searchTvShows(query: String, page: Int = 1) {
        viewModelScope.launch {
            // If it's the first page, show loading state
            if (page == 1) {
                _tvShowSearchState.value = SearchState.Loading
            } else {
                // For pagination, we keep the current results and show loading more
                _tvShowSearchState.value = SearchState.LoadingMore(
                    (_tvShowSearchState.value as? SearchState.Success)?.data ?: emptyList()
                )
            }
            
            try {
                val response = tmdbApi.searchTvShows(query, page)
                totalTvShowPages = response.totalPages
                
                // If it's the first page, replace the results
                if (page == 1) {
                    _tvShowSearchState.value = SearchState.Success(response.results)
                } else {
                    // For pagination, append the new results to the existing ones
                    val currentResults = (_tvShowSearchState.value as? SearchState.Success)?.data ?: emptyList()
                    _tvShowSearchState.value = SearchState.Success(currentResults + response.results)
                }
                
                currentTvShowPage = page
            } catch (e: Exception) {
                _tvShowSearchState.value = SearchState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Load more movie results (pagination)
     */
    fun loadMoreMovies() {
        if (currentMoviePage < totalMoviePages) {
            searchMovies(_searchQuery.value, currentMoviePage + 1)
        }
    }
    
    /**
     * Load more TV show results (pagination)
     */
    fun loadMoreTvShows() {
        if (currentTvShowPage < totalTvShowPages) {
            searchTvShows(_searchQuery.value, currentTvShowPage + 1)
        }
    }
    
    /**
     * Retry movie search
     */
    fun retryMovieSearch() {
        searchMovies(_searchQuery.value)
    }
    
    /**
     * Retry TV show search
     */
    fun retryTvShowSearch() {
        searchTvShows(_searchQuery.value)
    }
}

/**
 * State for search results
 */
sealed class SearchState<out T> {
    /**
     * Initial state (no search performed yet)
     */
    object Initial : SearchState<Nothing>()
    
    /**
     * Loading state
     */
    object Loading : SearchState<Nothing>()
    
    /**
     * Loading more state (for pagination)
     * @param data The current data
     */
    data class LoadingMore<T>(val data: List<T>) : SearchState<T>()
    
    /**
     * Success state
     * @param data The search results
     */
    data class Success<T>(val data: List<T>) : SearchState<T>()
    
    /**
     * Error state
     * @param message The error message
     */
    data class Error(val message: String) : SearchState<Nothing>()
}