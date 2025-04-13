package dev.sajidali.vod.discovery.data.db

import app.cash.sqldelight.db.SqlDriver

/**
 * Factory for creating platform-specific SQLDelight database drivers
 */
expect class DatabaseDriverFactory {
    /**
     * Creates a platform-specific SqlDriver for the WatchlistDatabase
     */
    fun createDriver(): SqlDriver
}
