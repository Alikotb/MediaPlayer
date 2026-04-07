package com.audio.tunoo.mediaplayer.di

import com.audio.tunoo.mediaplayer.model.repository.IRepository
import com.audio.tunoo.mediaplayer.model.repository.RepositoryImp
import org.koin.dsl.module

val repositoryModule = module {
    single<IRepository> {
        RepositoryImp(get(), get())
    }
}