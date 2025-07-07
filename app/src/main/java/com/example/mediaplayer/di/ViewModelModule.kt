package com.example.mediaplayer.di

import com.example.mediaplayer.features.tracks.TracksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { TracksViewModel(get()) }
}