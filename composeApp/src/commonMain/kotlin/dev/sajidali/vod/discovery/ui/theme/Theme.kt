package dev.sajidali.vod.discovery.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Light theme colors
private val LightColorPalette = lightColors(
    primary = PrimaryLight,
    primaryVariant = PrimaryVariantLight,
    secondary = SecondaryLight,
    secondaryVariant = SecondaryVariantLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    error = ErrorLight,
    onPrimary = OnPrimaryLight,
    onSecondary = OnSecondaryLight,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight,
    onError = OnErrorLight
)

// Dark theme colors
private val DarkColorPalette = darkColors(
    primary = PrimaryDark,
    primaryVariant = PrimaryVariantDark,
    secondary = SecondaryDark,
    secondaryVariant = SecondaryVariantDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    error = ErrorDark,
    onPrimary = OnPrimaryDark,
    onSecondary = OnSecondaryDark,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    onError = OnErrorDark
)

// Custom theme data class to hold additional theme properties
data class WhatToWatchThemeExtras(
    val movieCardBackground: Color,
    val ratingStarColor: Color,
    val favoriteIconColor: Color
)

// Default values for light theme extras
private val LightThemeExtras = WhatToWatchThemeExtras(
    movieCardBackground = Color(0xFFF5F5F5),
    ratingStarColor = Color(0xFFFFD700),
    favoriteIconColor = Color(0xFFE91E63)
)

// Default values for dark theme extras
private val DarkThemeExtras = WhatToWatchThemeExtras(
    movieCardBackground = Color(0xFF2D2D2D),
    ratingStarColor = Color(0xFFFFD700),
    favoriteIconColor = Color(0xFFE91E63)
)

// Composition local to provide theme extras
val LocalWhatToWatchThemeExtras = staticCompositionLocalOf { LightThemeExtras }

@Composable
fun WhatToWatchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette
    val themeExtras = if (darkTheme) DarkThemeExtras else LightThemeExtras

    CompositionLocalProvider(
        LocalWhatToWatchThemeExtras provides themeExtras
    ) {
        MaterialTheme(
            colors = colors,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

// Extension functions to access theme extras
object WhatToWatchTheme {
    val extras: WhatToWatchThemeExtras
        @Composable
        get() = LocalWhatToWatchThemeExtras.current
}