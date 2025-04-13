package dev.sajidali.vod.discovery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.sajidali.vod.discovery.ui.screens.movies.MoviesTab
import dev.sajidali.vod.discovery.ui.screens.search.SearchTab
import dev.sajidali.vod.discovery.ui.screens.tvshows.TVShowsTab
import dev.sajidali.vod.discovery.ui.screens.watchlist.WatchlistTab
import dev.sajidali.vod.discovery.ui.theme.WhatToWatchTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

// Main screen that wraps the TabNavigator
object MainScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(MoviesTab) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    BottomNavigation(
                        backgroundColor = MaterialTheme.colors.surface
                    ) {
                        val tabNavigator = LocalTabNavigator.current

                        BottomNavigationItem(
                            selected = tabNavigator.current == MoviesTab,
                            onClick = { tabNavigator.current = MoviesTab },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Movies") },
                            label = { Text("Movies") }
                        )

                        BottomNavigationItem(
                            selected = tabNavigator.current == TVShowsTab,
                            onClick = { tabNavigator.current = TVShowsTab },
                            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "TV Shows") },
                            label = { Text("TV Shows") }
                        )

                        BottomNavigationItem(
                            selected = tabNavigator.current == SearchTab,
                            onClick = { tabNavigator.current = SearchTab },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            label = { Text("Search") }
                        )

                        BottomNavigationItem(
                            selected = tabNavigator.current == WatchlistTab,
                            onClick = { tabNavigator.current = WatchlistTab },
                            icon = { Icon(Icons.Default.Favorite, contentDescription = "Watchlist") },
                            label = { Text("Watchlist") }
                        )
                    }
                }
            ) {
                CurrentTab()
            }
        }
    }
}

@Composable
@Preview
fun App() {
    WhatToWatchTheme {
        // Use Navigator to enable screen-to-screen navigation
        Navigator(MainScreen)
    }
}
