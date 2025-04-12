package dev.sajidali.vod.discovery

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "What to Watch",
    ) {
        App()
    }
}