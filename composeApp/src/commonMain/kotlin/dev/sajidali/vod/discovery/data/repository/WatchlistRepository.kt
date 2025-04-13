package dev.sajidali.vod.discovery.data.repository

import dev.sajidali.vod.discovery.data.model.ItemType
import dev.sajidali.vod.discovery.data.model.WatchlistItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing watchlist items
 */
interface WatchlistRepository {
    /**
     * Get all items in the watchlist
     * @return A flow of all watchlist items
     */
    suspend fun getAllItems(): Flow<List<WatchlistItem>>
    
    /**
     * Get a specific item from the watchlist by ID
     * @param id The ID of the item to get
     * @return The watchlist item, or null if not found
     */
    suspend fun getItemById(id: Int): WatchlistItem?
    
    /**
     * Get all items of a specific type from the watchlist
     * @param type The type of items to get
     * @return A flow of watchlist items of the specified type
     */
    suspend fun getItemsByType(type: ItemType): Flow<List<WatchlistItem>>
    
    /**
     * Add an item to the watchlist
     * @param item The item to add
     */
    suspend fun addItem(item: WatchlistItem)
    
    /**
     * Remove an item from the watchlist
     * @param id The ID of the item to remove
     */
    suspend fun removeItem(id: Int)
    
    /**
     * Check if an item is in the watchlist
     * @param id The ID of the item to check
     * @return True if the item is in the watchlist, false otherwise
     */
    suspend fun isInWatchlist(id: Int): Boolean
    
    /**
     * Clear all items from the watchlist
     */
    suspend fun clearWatchlist()
}