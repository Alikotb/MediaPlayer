package com.example.mediaplayer.model.repository

import com.example.mediaplayer.model.dto.AlbumsDto
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.FolderDto
import com.example.mediaplayer.model.dto.HistoryDto
import com.example.mediaplayer.model.dto.PlaylistDto
import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend fun getAllMusic(): Flow<List<AudioDto>>
    suspend fun getAllFolders():Flow<List<FolderDto>>
    suspend fun getAllAlbums(): Flow<List<AlbumsDto>>
    suspend fun insertAudioFile(audio: AudioDto)
    fun getAllMedia(): Flow<List<AudioDto>>
    suspend fun deleteMediaFile(audio: AudioDto)
    suspend fun isFav(audio: AudioDto): Boolean
    fun getAllHistory(): Flow<List<HistoryDto>>
    suspend fun insertHistoryFile(historyDto: HistoryDto)
    suspend fun insertNewPlayList(playlistDto: PlaylistDto)
    fun getAllPlaylists(): Flow<List<PlaylistDto>>
    suspend fun deletePlaylist(playlistDto: PlaylistDto)
     suspend fun getPlaylistByName(name: String) : PlaylistDto?
     suspend fun updatePlaylist(playlist: PlaylistDto)

}