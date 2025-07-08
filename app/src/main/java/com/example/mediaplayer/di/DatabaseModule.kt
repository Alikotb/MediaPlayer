package com.example.mediaplayer.di

import com.example.mediaplayer.model.local_data_source.dao.MediaDao
import com.example.mediaplayer.model.local_data_source.db.MediaDB
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {
    single {
        MediaDB.getInstance(androidContext())
    }
    single <MediaDao>{
        get<MediaDB>().getDao()
    }
}