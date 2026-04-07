package com.audio.tunoo.mediaplayer.di

import com.audio.tunoo.mediaplayer.features.albums.AlbumViewModel
import com.audio.tunoo.mediaplayer.features.audio_player.AudioPlayerViewModel
import com.audio.tunoo.mediaplayer.features.favorite.FavoriteViewModel
import com.audio.tunoo.mediaplayer.features.folders.FolderViewModel
import com.audio.tunoo.mediaplayer.features.history.HistoryViewModel
import com.audio.tunoo.mediaplayer.features.playlist.PlayListViewModel
import com.audio.tunoo.mediaplayer.features.search.SearchViewModel
import com.audio.tunoo.mediaplayer.features.tracks.TracksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { TracksViewModel(get()) }
    viewModel { FolderViewModel(get()) }
    viewModel { AlbumViewModel(get()) }
    viewModel { AudioPlayerViewModel(get()) }
    viewModel { FavoriteViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { PlayListViewModel(get()) }
}