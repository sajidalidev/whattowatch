package dev.sajidali.vod.discovery.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/**
 * iOS implementation of DatabaseDriverFactory
 */
actual class DatabaseDriverFactory actual constructor() {
    /**
     * Creates an iOS-specific SqlDriver for the WatchlistDatabase
     */
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = WatchlistDatabase.Schema,
            name = "watchlist.db"
        )
    }
}
