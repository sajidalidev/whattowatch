package dev.sajidali.vod.discovery

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.sajidali.vod.discovery.di.desktopModule

fun main() {
    // Initialize Koin before starting the application
    // Use the same API key as in Android and iOS for consistency
    Application.init("Your api key here", modules = listOf(desktopModule))

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "What to Watch",
        ) {
            App()
        }
    }
}
