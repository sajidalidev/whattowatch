package dev.sajidali.vod.discovery.ui.screens.tvshows

import dev.sajidali.vod.discovery.data.model.ItemType
import dev.sajidali.vod.discovery.data.model.WatchlistItem
import dev.sajidali.vod.discovery.data.model.toWatchlistItem
import dev.sajidali.vod.discovery.data.repository.WatchlistRepository
import dev.sajidali.vod.discovery.remote.api.TmdbApi
import dev.sajidali.vod.discovery.remote.model.Episode
import dev.sajidali.vod.discovery.remote.model.TvShowDetail
import dev.sajidali.vod.discovery.remote.model.TvShowSeasonResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the TV show detail screen
 */
class TvShowDetailViewModel(
    private val tmdbApi: TmdbApi,
    private val watchlistRepository: WatchlistRepository,
    private val tvShowId: Int
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // State for TV show details
    private val _tvShowDetailState = MutableStateFlow<TvShowDetailState>(TvShowDetailState.Loading)
    val tvShowDetailState: StateFlow<TvShowDetailState> = _tvShowDetailState.asStateFlow()

    // State for seasons with expanded state
    private val _seasonsState = MutableStateFlow<Map<Int, SeasonState>>(emptyMap())
    val seasonsState: StateFlow<Map<Int, SeasonState>> = _seasonsState.asStateFlow()

    // State for watchlist
    private val _isInWatchlist = MutableStateFlow(false)
    val isInWatchlist: StateFlow<Boolean> = _isInWatchlist.asStateFlow()

    init {
        loadTvShowDetails()
        checkIfInWatchlist()
    }

    /**
     * Load TV show details
     */
    fun loadTvShowDetails() {
        coroutineScope.launch {
            _tvShowDetailState.value = TvShowDetailState.Loading

            try {
                val tvShowDetail = tmdbApi.getTvShowDetails(tvShowId)
                _tvShowDetailState.value = TvShowDetailState.Success(tvShowDetail)

                // Initialize seasons state
                val initialSeasonsState = tvShowDetail.numberOfSeasons?.let { seasonCount ->
                    (1..seasonCount).associate { seasonNumber ->
                        seasonNumber to SeasonState(
                            isExpanded = false,
                            seasonData = null,
                            isLoading = false
                        )
                    }
                } ?: emptyMap()

                _seasonsState.value = initialSeasonsState
            } catch (e: Exception) {
                _tvShowDetailState.value = TvShowDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Toggle the expanded state of a season and load its details if not already loaded
     */
    fun toggleSeasonExpanded(seasonNumber: Int) {
        val currentSeasonsState = _seasonsState.value.toMutableMap()
        val currentSeasonState = currentSeasonsState[seasonNumber]

        if (currentSeasonState != null) {
            // Toggle expanded state
            val newIsExpanded = !currentSeasonState.isExpanded

            // If expanding and no data is loaded yet, load the season data
            if (newIsExpanded && currentSeasonState.seasonData == null && !currentSeasonState.isLoading) {
                currentSeasonsState[seasonNumber] = currentSeasonState.copy(isLoading = true)
                _seasonsState.value = currentSeasonsState

                loadSeasonDetails(seasonNumber)
            } else {
                // Just toggle the expanded state
                currentSeasonsState[seasonNumber] = currentSeasonState.copy(isExpanded = newIsExpanded)
                _seasonsState.value = currentSeasonsState
            }
        }
    }

    /**
     * Check if the TV show is in the watchlist
     */
    private fun checkIfInWatchlist() {
        coroutineScope.launch {
            _isInWatchlist.value = watchlistRepository.isInWatchlist(tvShowId)
        }
    }

    /**
     * Toggle the TV show's watchlist status
     */
    fun toggleWatchlist() {
        coroutineScope.launch {
            val currentState = _tvShowDetailState.value
            if (currentState is TvShowDetailState.Success) {
                val tvShow = currentState.tvShowDetail

                if (_isInWatchlist.value) {
                    // Remove from watchlist
                    watchlistRepository.removeItem(tvShowId)
                } else {
                    // Add to watchlist
                    val watchlistItem = tvShow.toWatchlistItem()
                    watchlistRepository.addItem(watchlistItem)
                }

                // Update state
                _isInWatchlist.value = !_isInWatchlist.value
            }
        }
    }

    /**
     * Load season details
     */
    private fun loadSeasonDetails(seasonNumber: Int) {
        coroutineScope.launch {
            try {
                val seasonDetails = tmdbApi.getTvShowSeasonDetails(tvShowId, seasonNumber)

                val currentSeasonsState = _seasonsState.value.toMutableMap()
                val currentSeasonState = currentSeasonsState[seasonNumber]

                if (currentSeasonState != null) {
                    currentSeasonsState[seasonNumber] = currentSeasonState.copy(
                        seasonData = seasonDetails,
                        isLoading = false
                    )
                    _seasonsState.value = currentSeasonsState
                }
            } catch (e: Exception) {
                val currentSeasonsState = _seasonsState.value.toMutableMap()
                val currentSeasonState = currentSeasonsState[seasonNumber]

                if (currentSeasonState != null) {
                    currentSeasonsState[seasonNumber] = currentSeasonState.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                    _seasonsState.value = currentSeasonsState
                }
            }
        }
    }
}

/**
 * State for TV show details
 */
sealed class TvShowDetailState {
    /**
     * Loading state
     */
    object Loading : TvShowDetailState()

    /**
     * Success state
     * @param tvShowDetail The TV show details
     */
    data class Success(val tvShowDetail: TvShowDetail) : TvShowDetailState()

    /**
     * Error state
     * @param message The error message
     */
    data class Error(val message: String) : TvShowDetailState()
}

/**
 * State for a season
 */
data class SeasonState(
    val isExpanded: Boolean,
    val seasonData: TvShowSeasonResponse?,
    val isLoading: Boolean,
    val error: String? = null
)

/**
 * Extension function to convert a TvShowDetail to a WatchlistItem
 */
fun TvShowDetail.toWatchlistItem() = WatchlistItem(
    id = id,
    title = name,
    posterPath = posterPath,
    overview = overview,
    releaseDate = firstAirDate,
    voteAverage = voteAverage,
    itemType = ItemType.TV_SHOW
)
