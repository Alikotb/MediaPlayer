package com.example.mediaplayer

import android.app.Application
import com.example.mediaplayer.di.mediaSourceImpModule
import com.example.mediaplayer.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MediaApp:Application() {
    override fun onCreate() {
        super.onCreate()
        registerKoin()
    }

    private fun registerKoin() {
        val modules = listOf(
            mediaSourceImpModule,
            repositoryModule
        )
        startKoin {
            androidContext(this@MediaApp)
            modules(modules)
        }

    }
}