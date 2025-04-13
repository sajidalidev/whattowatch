package dev.sajidali.vod.discovery.di

import dev.sajidali.vod.discovery.data.db.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {

    single { DatabaseDriverFactory(androidContext()) }

}