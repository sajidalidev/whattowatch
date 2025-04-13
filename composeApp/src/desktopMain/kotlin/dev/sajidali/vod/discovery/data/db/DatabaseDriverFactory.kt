package dev.sajidali.vod.discovery.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

/**
 * Desktop implementation of DatabaseDriverFactory
 */
actual class DatabaseDriverFactory() {
    /**
     * Creates a desktop-specific SqlDriver for the WatchlistDatabase
     */
    actual fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        WatchlistDatabase.Schema.create(driver)
        return driver
    }
}
