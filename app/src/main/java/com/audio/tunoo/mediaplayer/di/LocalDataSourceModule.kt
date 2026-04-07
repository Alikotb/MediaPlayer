package com.audio.tunoo.mediaplayer.di

import com.audio.tunoo.mediaplayer.model.local_data_source.data_source.ILocalDataSource
import com.audio.tunoo.mediaplayer.model.local_data_source.data_source.LocalDataSourceImp
import org.koin.dsl.module

val localDataSourceModule = module {
    single<ILocalDataSource> { LocalDataSourceImp(get()) }
}
