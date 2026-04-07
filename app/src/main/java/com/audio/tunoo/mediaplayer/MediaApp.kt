package com.audio.tunoo.mediaplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.audio.tunoo.mediaplayer.di.dbModule
import com.audio.tunoo.mediaplayer.di.localDataSourceModule
import com.audio.tunoo.mediaplayer.di.mediaSourceImpModule
import com.audio.tunoo.mediaplayer.di.repositoryModule
import com.audio.tunoo.mediaplayer.di.viewModelModule
import com.audio.tunoo.mediaplayer.utils.MediaConstant
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MediaApp:Application() {
    override fun onCreate() {
        super.onCreate()
        registerKoin()
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                MediaConstant.CHANNEL_ID,
                MediaConstant.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT // ← FIX
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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