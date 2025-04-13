package dev.sajidali.vod.discovery.data.model

import dev.sajidali.vod.discovery.remote.model.Movie
import dev.sajidali.vod.discovery.remote.model.TvShow
import dev.sajidali.vod.discovery.util.getCurrentTimeMillis

/**
 * Represents the type of item in the watchlist
 */
enum class ItemType {
    MOVIE, TV_SHOW
}

/**
 * Represents an item in the watchlist
 */
data class WatchlistItem(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val overview: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val itemType: ItemType,
    val addedDate: Long = getCurrentTimeMillis()
)

/**
 * Extension function to convert a Movie to a WatchlistItem
 */
fun Movie.toWatchlistItem(): WatchlistItem {
    return WatchlistItem(
        id = id,
        title = title,
        posterPath = posterPath,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        itemType = ItemType.MOVIE
    )
}

/**
 * Extension function to convert a TvShow to a WatchlistItem
 */
fun TvShow.toWatchlistItem(): WatchlistItem {
    return WatchlistItem(
        id = id,
        title = name,
        posterPath = posterPath,
        overview = overview,
        releaseDate = firstAirDate,
        voteAverage = voteAverage,
        itemType = ItemType.TV_SHOW
    )
}
