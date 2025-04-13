# Watchlist Implementation Plan

## Overview
This document outlines the plan for implementing a watchlist feature in the "What to Watch" application. The watchlist will allow users to save movies and TV shows to view later.

## 1. Add SQLDelight Dependencies
SQLDelight will be used for local database storage of watchlist items.

### Update gradle/libs.versions.toml
```toml
[versions]
# Add SQLDelight version
sqldelight = "2.0.1"

[libraries]
# Add SQLDelight libraries
sqldelight-runtime = { group = "app.cash.sqldelight", name = "runtime", version.ref = "sqldelight" }
sqldelight-coroutines-extensions = { group = "app.cash.sqldelight", name = "coroutines-extensions", version.ref = "sqldelight" }
sqldelight-primitive-adapters = { group = "app.cash.sqldelight", name = "primitive-adapters", version.ref = "sqldelight" }
sqldelight-android-driver = { group = "app.cash.sqldelight", name = "android-driver", version.ref = "sqldelight" }
sqldelight-native-driver = { group = "app.cash.sqldelight", name = "native-driver", version.ref = "sqldelight" }
sqldelight-sqlite-driver = { group = "app.cash.sqldelight", name = "sqlite-driver", version.ref = "sqldelight" }

[plugins]
# Add SQLDelight plugin
sqldelight = { id = "app.cash.sqldelight", version.ref = "sqldelight" }
```

### Update composeApp/build.gradle.kts
```kotlin
plugins {
    // Add SQLDelight plugin
    alias(libs.plugins.sqldelight)
}

// Add SQLDelight configuration
sqldelight {
    databases {
        create("WatchlistDatabase") {
            packageName.set("dev.sajidali.vod.discovery.data.db")
        }
    }
}

// Add platform-specific SQLDelight dependencies
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.sqldelight.primitive.adapters)
        }
        
        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
        }
        
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
        
        desktopMain.dependencies {
            implementation(libs.sqldelight.sqlite.driver)
        }
    }
}
```

## 2. Create Database Schema
Create the SQLDelight schema file for the watchlist table.

### Create WatchlistDatabase.sq
```sql
CREATE TABLE WatchlistItem (
    id INTEGER NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    posterPath TEXT,
    overview TEXT,
    releaseDate TEXT,
    voteAverage REAL,
    itemType TEXT NOT NULL,
    addedDate INTEGER NOT NULL
);

getAllItems:
SELECT * FROM WatchlistItem ORDER BY addedDate DESC;

getItemById:
SELECT * FROM WatchlistItem WHERE id = ?;

getItemsByType:
SELECT * FROM WatchlistItem WHERE itemType = ? ORDER BY addedDate DESC;

insertItem:
INSERT OR REPLACE INTO WatchlistItem(id, title, posterPath, overview, releaseDate, voteAverage, itemType, addedDate)
VALUES (?, ?, ?, ?, ?, ?, ?, ?);

deleteItem:
DELETE FROM WatchlistItem WHERE id = ?;

isInWatchlist:
SELECT COUNT(*) FROM WatchlistItem WHERE id = ?;

clearWatchlist:
DELETE FROM WatchlistItem;
```

## 3. Create Watchlist Data Layer

### Create WatchlistRepository
Create an interface and implementation for the watchlist repository.

```kotlin
interface WatchlistRepository {
    suspend fun getAllItems(): Flow<List<WatchlistItem>>
    suspend fun getItemById(id: Int): WatchlistItem?
    suspend fun getItemsByType(type: String): Flow<List<WatchlistItem>>
    suspend fun addItem(item: WatchlistItem)
    suspend fun removeItem(id: Int)
    suspend fun isInWatchlist(id: Int): Boolean
    suspend fun clearWatchlist()
}
```

### Create Database Factory
Create a factory for the SQLDelight database that handles platform-specific implementations.

```kotlin
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

// Android implementation
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(WatchlistDatabase.Schema, context, "watchlist.db")
    }
}

// iOS implementation
actual class DatabaseDriverFactory() {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(WatchlistDatabase.Schema, "watchlist.db")
    }
}

// Desktop implementation
actual class DatabaseDriverFactory() {
    actual fun createDriver(): SqlDriver {
        return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
            WatchlistDatabase.Schema.create(it)
        }
    }
}
```

## 4. Integrate Watchlist with Movies and TV Shows

### Create Watchlist Models
Create models for watchlist items that can represent both movies and TV shows.

```kotlin
data class WatchlistItem(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val overview: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val itemType: ItemType,
    val addedDate: Long = System.currentTimeMillis()
)

enum class ItemType {
    MOVIE, TV_SHOW
}

// Extension functions to convert Movie and TvShow to WatchlistItem
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
```

### Update View Models
Add watchlist functionality to movie and TV show view models.

```kotlin
class MovieDetailViewModel(
    private val tmdbApi: TmdbApi,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {
    // Existing code...
    
    private val _isInWatchlist = MutableStateFlow(false)
    val isInWatchlist: StateFlow<Boolean> = _isInWatchlist
    
    fun checkIfInWatchlist(movieId: Int) {
        viewModelScope.launch {
            _isInWatchlist.value = watchlistRepository.isInWatchlist(movieId)
        }
    }
    
    fun toggleWatchlist(movie: Movie) {
        viewModelScope.launch {
            if (_isInWatchlist.value) {
                watchlistRepository.removeItem(movie.id)
            } else {
                watchlistRepository.addItem(movie.toWatchlistItem())
            }
            _isInWatchlist.value = !_isInWatchlist.value
        }
    }
}

// Similar updates for TvShowDetailViewModel
```

## 5. Create Watchlist UI

### Create WatchlistViewModel
```kotlin
class WatchlistViewModel(
    private val watchlistRepository: WatchlistRepository,
    private val tmdbApi: TmdbApi
) : ViewModel() {
    private val _watchlistItems = MutableStateFlow<List<WatchlistItem>>(emptyList())
    val watchlistItems: StateFlow<List<WatchlistItem>> = _watchlistItems
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    init {
        loadWatchlist()
    }
    
    fun loadWatchlist() {
        viewModelScope.launch {
            _isLoading.value = true
            watchlistRepository.getAllItems().collect {
                _watchlistItems.value = it
                _isLoading.value = false
            }
        }
    }
    
    fun removeFromWatchlist(id: Int) {
        viewModelScope.launch {
            watchlistRepository.removeItem(id)
            loadWatchlist()
        }
    }
    
    fun clearWatchlist() {
        viewModelScope.launch {
            watchlistRepository.clearWatchlist()
            loadWatchlist()
        }
    }
}
```

### Update WatchlistScreen
Update the WatchlistScreen to display watchlist items.

```kotlin
@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel = koinViewModel()
) {
    val watchlistItems by viewModel.watchlistItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (watchlistItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Your watchlist is empty.\nAdd movies and TV shows to see them here.",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn {
                items(watchlistItems) { item ->
                    WatchlistItemRow(
                        item = item,
                        onItemClick = { /* Navigate to detail */ },
                        onRemoveClick = { viewModel.removeFromWatchlist(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun WatchlistItemRow(
    item: WatchlistItem,
    onItemClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    // UI for a watchlist item row
}
```

## 6. Update Dependency Injection
Update the Koin module to provide the new dependencies.

```kotlin
val dataModule = module {
    single { DatabaseDriverFactory(get()) }
    single { WatchlistDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single<WatchlistRepository> { WatchlistRepositoryImpl(get()) }
}

val viewModelModule = module {
    // Existing view models...
    viewModel { WatchlistViewModel(get(), get()) }
}
```

## 7. Testing
Test the watchlist functionality:
- Adding items to the watchlist
- Removing items from the watchlist
- Displaying the watchlist
- Persistence across app restarts