package com.example.mediaplayer.di

import com.example.mediaplayer.model.local_data_source.data_source.ILocalDataSource
import com.example.mediaplayer.model.local_data_source.data_source.LocalDataSourceImp
import org.koin.dsl.module

val localDataSourceModule = module {
    single <ILocalDataSource>{ LocalDataSourceImp(get()) }
}