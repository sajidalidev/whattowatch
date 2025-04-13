package dev.sajidali.vod.discovery.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/**
 * Android implementation of DatabaseDriverFactory
 */
actual class DatabaseDriverFactory(private val context: Context) {

    /**
     * Creates an Android-specific SqlDriver for the WatchlistDatabase
     */
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = WatchlistDatabase.Schema,
            context = context,
            name = "watchlist.db"
        )
    }
}
