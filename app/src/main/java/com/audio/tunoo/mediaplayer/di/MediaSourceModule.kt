package com.audio.tunoo.mediaplayer.di

import com.audio.tunoo.mediaplayer.model.media_source.IMediaSource
import com.audio.tunoo.mediaplayer.model.media_source.MediaSourceImp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mediaSourceImpModule =
    module {
        single<IMediaSource> { MediaSourceImp(androidContext()) }
    }