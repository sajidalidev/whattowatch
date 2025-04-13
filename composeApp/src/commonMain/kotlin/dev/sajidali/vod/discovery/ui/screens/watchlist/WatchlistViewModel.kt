package dev.sajidali.vod.discovery.ui.screens.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sajidali.vod.discovery.data.model.WatchlistItem
import dev.sajidali.vod.discovery.data.repository.WatchlistRepository
import dev.sajidali.vod.discovery.remote.api.TmdbApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the watchlist screen
 */
class WatchlistViewModel(
    private val watchlistRepository: WatchlistRepository,
    private val tmdbApi: TmdbApi
) : ViewModel() {
    
    private val _watchlistItems = MutableStateFlow<List<WatchlistItem>>(emptyList())
    val watchlistItems: StateFlow<List<WatchlistItem>> = _watchlistItems.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadWatchlist()
    }
    
    /**
     * Load all items in the watchlist
     */
    fun loadWatchlist() {
        viewModelScope.launch {
            _isLoading.value = true
            watchlistRepository.getAllItems().collect {
                _watchlistItems.value = it
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Remove an item from the watchlist
     * @param id The ID of the item to remove
     */
    fun removeFromWatchlist(id: Int) {
        viewModelScope.launch {
            watchlistRepository.removeItem(id)
            loadWatchlist()
        }
    }
    
    /**
     * Clear all items from the watchlist
     */
    fun clearWatchlist() {
        viewModelScope.launch {
            watchlistRepository.clearWatchlist()
            loadWatchlist()
        }
    }
    
    /**
     * Get the poster URL for an item
     * @param posterPath The poster path
     * @return The full URL to the poster image
     */
    fun getPosterUrl(posterPath: String?): String? {
        return tmdbApi.getPosterUrl(posterPath)
    }
}