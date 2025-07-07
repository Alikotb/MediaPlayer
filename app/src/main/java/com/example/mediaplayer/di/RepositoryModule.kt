package com.example.mediaplayer.di

import com.example.mediaplayer.model.repository.IRepository
import com.example.mediaplayer.model.repository.RepositoryImp
import org.koin.dsl.module

val repositoryModule = module {
    single<IRepository> {
        RepositoryImp(get())
    }
}