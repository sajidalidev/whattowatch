package dev.sajidali.vod.discovery.remote.api

import dev.sajidali.vod.discovery.remote.model.GenreListResponse
import dev.sajidali.vod.discovery.remote.model.MovieDetail
import dev.sajidali.vod.discovery.remote.model.MovieListResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Implementation of the TMDb API client using Ktor
 * @param apiKey The TMDb API key
 * @param client The HttpClient to use for requests
 */
class TmdbApiImpl(
    private val apiKey: String,
    private val client: HttpClient
) : TmdbApi {

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3"
        private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p"
    }

    override suspend fun getTrendingMovies(timeWindow: String, page: Int): MovieListResponse {
        return client.get("$BASE_URL/trending/movie/$timeWindow") {
            parameter("api_key", apiKey)
            parameter("page", page)
        }.body()
    }

    override suspend fun getTopRatedMovies(page: Int): MovieListResponse {
        return client.get("$BASE_URL/movie/top_rated") {
            parameter("api_key", apiKey)
            parameter("page", page)
        }.body()
    }

    override suspend fun searchMovies(query: String, page: Int): MovieListResponse {
        return client.get("$BASE_URL/search/movie") {
            parameter("api_key", apiKey)
            parameter("query", query)
            parameter("page", page)
        }.body()
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetail {
        return client.get("$BASE_URL/movie/$movieId") {
            parameter("api_key", apiKey)
        }.body()
    }

    override suspend fun getGenres(): GenreListResponse {
        return client.get("$BASE_URL/genre/movie/list") {
            parameter("api_key", apiKey)
        }.body()
    }

    override fun buildImageUrl(path: String?, size: String): String? {
        return if (path != null) {
            "$IMAGE_BASE_URL/$size$path"
        } else {
            null
        }
    }
}
