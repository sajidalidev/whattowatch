package dev.sajidali.vod.discovery.di

import dev.sajidali.vod.discovery.data.db.DatabaseDriverFactory
import org.koin.dsl.module

val desktopModule = module {

    single { DatabaseDriverFactory() }

}