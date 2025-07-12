package com.example.mediaplayer.model.repository

import com.example.mediaplayer.model.dto.AlbumsDto
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.FolderDto
import com.example.mediaplayer.model.dto.HistoryDto
import com.example.mediaplayer.model.dto.PlaylistDto
import com.example.mediaplayer.model.local_data_source.data_source.ILocalDataSource
import com.example.mediaplayer.model.media_source.IMediaSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RepositoryImp(
    private val mediaSource: IMediaSource,
    private val localDataSource: ILocalDataSource
) : IRepository {

    override suspend fun getAllMusic(): Flow<List<AudioDto>> {
        return flowOf(mediaSource.getAllMusic())
    }

    override suspend fun getAllFolders(): Flow<List<FolderDto>> {
        return flowOf(mediaSource.getAllFolders())
    }

    override suspend fun getAllAlbums(): Flow<List<AlbumsDto>> {
        return flowOf(mediaSource.getAllAlbums())
    }

    override suspend fun insertAudioFile(audio: AudioDto) {
        localDataSource.insertAudioFile(audio)
    }

    override fun getAllMedia() = localDataSource.getAllMedia()

    override suspend fun deleteMediaFile(audio: AudioDto) {
        localDataSource.deleteMediaFile(audio)
    }

    override suspend fun isFav(audio: AudioDto) = localDataSource.isFav(audio)
    override fun getAllHistory() = localDataSource.getAllHistory()

    override suspend fun insertHistoryFile(historyDto: HistoryDto) {
        localDataSource.insertHistoryFile(historyDto)
    }

    override suspend fun insertNewPlayList(playlistDto: PlaylistDto) {
        localDataSource.insertNewPlayList(playlistDto)
    }

    override fun getAllPlaylists() = localDataSource.getAllPlaylists()

    override suspend fun deletePlaylist(playlistDto: PlaylistDto) {
        localDataSource.deletePlaylist(playlistDto)
    }

    override suspend fun getPlaylistByName(name: String) = localDataSource.getPlaylistByName(name)

    override suspend fun updatePlaylist(playlist: PlaylistDto) {
        localDataSource.updatePlaylist(playlist)
    }

}