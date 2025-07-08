package com.example.mediaplayer

import android.app.Application
import com.example.mediaplayer.di.dbModule
import com.example.mediaplayer.di.localDataSourceModule
import com.example.mediaplayer.di.mediaSourceImpModule
import com.example.mediaplayer.di.repositoryModule
import com.example.mediaplayer.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MediaApp:Application() {
    override fun onCreate() {
        super.onCreate()
        registerKoin()
    }

    private fun registerKoin() {
        val modules = listOf(
            dbModule,
            localDataSourceModule,
            mediaSourceImpModule,
            repositoryModule,
            viewModelModule
        )
        startKoin {
            androidContext(this@MediaApp)
            modules(modules)
        }

    }
}