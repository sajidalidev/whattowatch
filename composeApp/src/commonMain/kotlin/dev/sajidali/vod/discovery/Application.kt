package dev.sajidali.vod.discovery

import dev.sajidali.vod.discovery.di.AppModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Application class for initializing dependencies
 */
object Application {
    /**
     * Initialize the application
     * @param apiKey The TMDb API key
     * @param appDeclaration Additional Koin configuration
     */
    fun init(apiKey: String, appDeclaration: KoinAppDeclaration = {}) {
        startKoin {
            appDeclaration()
            modules(AppModule.create(apiKey))
        }
    }
}