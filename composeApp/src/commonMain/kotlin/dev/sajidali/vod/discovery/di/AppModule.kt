package dev.sajidali.vod.discovery.di

import dev.sajidali.vod.discovery.data.db.DatabaseDriverFactory
import dev.sajidali.vod.discovery.data.db.WatchlistDatabase
import dev.sajidali.vod.discovery.data.repository.WatchlistRepository
import dev.sajidali.vod.discovery.data.repository.WatchlistRepositoryImpl
import dev.sajidali.vod.discovery.remote.api.TmdbApi
import dev.sajidali.vod.discovery.remote.api.TmdbApiImpl
import dev.sajidali.vod.discovery.ui.screens.moviedetails.MovieDetailsViewModel
import dev.sajidali.vod.discovery.ui.screens.movies.MoviesViewModel
import dev.sajidali.vod.discovery.ui.screens.search.SearchViewModel
import dev.sajidali.vod.discovery.ui.screens.tvshows.TvShowDetailViewModel
import dev.sajidali.vod.discovery.ui.screens.tvshows.TvShowsViewModel
import dev.sajidali.vod.discovery.ui.screens.watchlist.WatchlistViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module for the application
 */
object AppModule {
    /**
     * Create the application module
     * @param apiKey The TMDb API key
     * @return The application module
     */
    fun create(apiKey: String): Module = module {
        // HttpClient
        single {
            HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                        prettyPrint = true
                        isLenient = true
                    })
                }
                install(Logging) {
                    level = LogLevel.INFO
                }
            }
        }

        // TmdbApi
        single<TmdbApi> { TmdbApiImpl(apiKey, get()) }

        // Database
        single { WatchlistDatabase(get<DatabaseDriverFactory>().createDriver()) }

        // Repositories
        single<WatchlistRepository> { WatchlistRepositoryImpl(get()) }

        // ViewModels
        factory { MoviesViewModel(get()) }
        factory { TvShowsViewModel(get()) }
        factory { SearchViewModel(get()) }
        factory { parameters -> MovieDetailsViewModel(get(), get(), parameters.get()) }
        factory { parameters -> TvShowDetailViewModel(get(), get(), parameters.get()) }
        factory { WatchlistViewModel(get(), get()) }
    }
}
