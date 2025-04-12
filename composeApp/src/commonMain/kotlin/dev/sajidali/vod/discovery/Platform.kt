package dev.sajidali.vod.discovery

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform