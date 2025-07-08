package com.example.mediaplayer.di

import com.example.mediaplayer.features.albums.AlbumViewModel
import com.example.mediaplayer.features.audio_player.AudioPlayerViewModel
import com.example.mediaplayer.features.favorite.FavoriteViewModel
import com.example.mediaplayer.features.folders.FolderViewModel
import com.example.mediaplayer.features.tracks.TracksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { TracksViewModel(get()) }
    viewModel { FolderViewModel(get()) }
    viewModel { AlbumViewModel(get()) }
    viewModel { AudioPlayerViewModel(get()) }
    viewModel { FavoriteViewModel(get()) }
}