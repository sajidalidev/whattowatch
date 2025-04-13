package dev.sajidali.vod.discovery.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.sajidali.vod.discovery.data.db.WatchlistDatabase
import dev.sajidali.vod.discovery.data.model.ItemType
import dev.sajidali.vod.discovery.data.model.WatchlistItem
import dev.sajidali.vod.discovery.util.getCurrentTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementation of [WatchlistRepository] that uses SQLDelight
 */
class WatchlistRepositoryImpl(
    private val database: WatchlistDatabase
) : WatchlistRepository {

    private val queries = database.watchlistDatabaseQueries

    /**
     * Get all items in the watchlist
     * @return A flow of all watchlist items
     */
    override suspend fun getAllItems(): Flow<List<WatchlistItem>> = withContext(Dispatchers.Default) {
        queries.getAllItems()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { dbItems ->
                dbItems.map { it.toWatchlistItem() }
            }
    }

    /**
     * Get a specific item from the watchlist by ID
     * @param id The ID of the item to get
     * @return The watchlist item, or null if not found
     */
    override suspend fun getItemById(id: Int): WatchlistItem? = withContext(Dispatchers.Default) {
        queries.getItemById(id.toLong())
            .executeAsOneOrNull()
            ?.toWatchlistItem()
    }

    /**
     * Get all items of a specific type from the watchlist
     * @param type The type of items to get
     * @return A flow of watchlist items of the specified type
     */
    override suspend fun getItemsByType(type: ItemType): Flow<List<WatchlistItem>> = withContext(Dispatchers.Default) {
        queries.getItemsByType(type.name)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { dbItems ->
                dbItems.map { it.toWatchlistItem() }
            }
    }

    /**
     * Add an item to the watchlist
     * @param item The item to add
     */
    override suspend fun addItem(item: WatchlistItem) = withContext(Dispatchers.Default) {
        queries.insertItem(
            id = item.id.toLong(),
            title = item.title,
            posterPath = item.posterPath,
            overview = item.overview,
            releaseDate = item.releaseDate,
            voteAverage = item.voteAverage,
            itemType = item.itemType.name,
            addedDate = item.addedDate
        )
    }

    /**
     * Remove an item from the watchlist
     * @param id The ID of the item to remove
     */
    override suspend fun removeItem(id: Int) = withContext(Dispatchers.Default) {
        queries.deleteItem(id.toLong())
    }

    /**
     * Check if an item is in the watchlist
     * @param id The ID of the item to check
     * @return True if the item is in the watchlist, false otherwise
     */
    override suspend fun isInWatchlist(id: Int): Boolean = withContext(Dispatchers.Default) {
        queries.isInWatchlist(id.toLong()).executeAsOne() > 0
    }

    /**
     * Clear all items from the watchlist
     */
    override suspend fun clearWatchlist() = withContext(Dispatchers.Default) {
        queries.clearWatchlist()
    }

    /**
     * Convert a database item to a domain model
     */
    private fun dev.sajidali.vod.discovery.data.db.WatchlistItem.toWatchlistItem(): WatchlistItem {
        return WatchlistItem(
            id = id.toInt(),
            title = title,
            posterPath = posterPath,
            overview = overview,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            itemType = ItemType.valueOf(itemType),
            addedDate = addedDate
        )
    }
}