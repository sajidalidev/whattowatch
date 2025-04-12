# TMDb API Client for Kotlin Multiplatform

This package provides a Kotlin Multiplatform (KMP) client for the TMDb (The Movie Database) API using Ktor.

## Features

The client supports the following endpoints from TMDb v3:

1. Get trending movies
2. Get top-rated movies
3. Search for movies by query
4. Get movie details by ID
5. Get list of genres

Additionally, it provides a helper function to build image URLs for posters and backdrops.

## Implementation Details

- Uses Ktor client with Json serialization (kotlinx.serialization)
- Defines data classes for responses
- API key is injected (not hardcoded)
- Uses Koin for dependency injection
- Works with Kotlin Multiplatform (iOS + Android + Desktop)
- Base URL: https://api.themoviedb.org/3/

## Usage

### Setting Up Dependency Injection

First, initialize Koin with the TMDb API module:

```kotlin
// Initialize Koin with your TMDb API key
Application.init("your_api_key_here")
```

### Injecting the API Client

You can inject the TMDb API client in your classes using Koin:

```kotlin
class MyViewModel : KoinComponent {
    // Inject the TMDb API client
    private val tmdbApi: TmdbApi by inject()

    // Use the API client
    suspend fun getTrendingMovies() = tmdbApi.getTrendingMovies()
}
```

### Getting Trending Movies

```kotlin
// Get trending movies for the week (default)
val trendingMovies = tmdbApi.getTrendingMovies()

// Get trending movies for the day
val trendingMoviesDay = tmdbApi.getTrendingMovies(timeWindow = "day")

// Get trending movies with pagination
val trendingMoviesPage2 = tmdbApi.getTrendingMovies(page = 2)
```

### Getting Top-Rated Movies

```kotlin
// Get top-rated movies
val topRatedMovies = tmdbApi.getTopRatedMovies()

// Get top-rated movies with pagination
val topRatedMoviesPage2 = tmdbApi.getTopRatedMovies(page = 2)
```

### Searching for Movies

```kotlin
// Search for movies by query
val searchResults = tmdbApi.searchMovies("Avengers")

// Search for movies with pagination
val searchResultsPage2 = tmdbApi.searchMovies("Avengers", page = 2)
```

### Getting Movie Details

```kotlin
// Get movie details by ID
val movieDetails = tmdbApi.getMovieDetails(24428) // The Avengers
```

### Getting Genres

```kotlin
// Get list of genres
val genres = tmdbApi.getGenres()
```

### Building Image URLs

```kotlin
// Build an image URL for a poster
val posterUrl = tmdbApi.buildImageUrl(movie.posterPath)

// Build an image URL for a backdrop with a specific size
val backdropUrl = tmdbApi.buildImageUrl(movie.backdropPath, size = "original")
```

## Error Handling

All API methods are suspending functions that may throw exceptions. It's recommended to wrap API calls in try-catch blocks:

```kotlin
try {
    val trendingMovies = tmdbApi.getTrendingMovies()
    // Process the response
} catch (e: Exception) {
    // Handle the error
    println("Error: ${e.message}")
}
```

## Complete Example

See the `TmdbApiExample.kt` file for a complete example of how to use the TMDb API client.
