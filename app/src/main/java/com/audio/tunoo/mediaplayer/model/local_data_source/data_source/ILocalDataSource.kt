package com.audio.tunoo.mediaplayer.model.local_data_source.data_source

import com.audio.tunoo.mediaplayer.model.dto.AudioDto
import com.audio.tunoo.mediaplayer.model.dto.HistoryDto
import com.audio.tunoo.mediaplayer.model.dto.PlaylistDto
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    suspend fun insertAudioFile(audio: AudioDto)
    fun getAllMedia(): Flow<List<AudioDto>>
    suspend fun deleteMediaFile(audio: AudioDto)
    suspend fun isFav(audio: AudioDto): Boolean
    fun getAllHistory(): Flow<List<HistoryDto>>
    suspend fun insertHistoryFile(historyDto: HistoryDto)
    suspend fun insertNewPlayList(playlistDto: PlaylistDto)
    fun getAllPlaylists(): Flow<List<PlaylistDto>>
    suspend fun deletePlaylist(playlistDto: PlaylistDto)

    suspend fun getPlaylistByName(name: String): PlaylistDto?

    suspend fun updatePlaylist(playlist: PlaylistDto)
}
