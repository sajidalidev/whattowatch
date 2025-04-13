package dev.sajidali.vod.discovery

import androidx.compose.ui.window.ComposeUIViewController
import dev.sajidali.vod.discovery.di.iosModule
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    // Initialize Koin before creating the view controller
    // Use the same API key as in Android for consistency
    Application.init("Your api key here", modules = listOf(iosModule))

    return ComposeUIViewController { App() }
}
