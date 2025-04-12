package dev.sajidali.vod.discovery.di

import dev.sajidali.vod.discovery.remote.api.TmdbApi
import dev.sajidali.vod.discovery.remote.api.TmdbApiImpl
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
    }
}