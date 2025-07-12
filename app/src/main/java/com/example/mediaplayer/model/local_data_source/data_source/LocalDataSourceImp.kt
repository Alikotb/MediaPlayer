package com.example.mediaplayer.model.local_data_source.data_source

import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.HistoryDto
import com.example.mediaplayer.model.dto.PlaylistDto
import com.example.mediaplayer.model.local_data_source.dao.MediaDao

class LocalDataSourceImp(private val db: MediaDao): ILocalDataSource {

    override suspend fun insertAudioFile(audio: AudioDto) {
        db.insertAudioFile(audio)
    }

    override fun getAllMedia() = db.getAllMedia()

    override suspend fun deleteMediaFile(audio: AudioDto) {
        db.deleteMediaFile(audio)
    }

    override suspend fun isFav(audio: AudioDto): Boolean {
        return db.getMediaById(audio.id) != null
    }

    override fun getAllHistory() = db.getAllHistory()

    override suspend fun insertHistoryFile(historyDto: HistoryDto) {
        db.insertHistoryFile(historyDto)
    }

    override suspend fun insertNewPlayList(playlistDto: PlaylistDto) {
        db.insertNewPlayList(playlistDto)
    }

    override fun getAllPlaylists() = db.getAllPlaylists()

    override suspend fun deletePlaylist(playlistDto: PlaylistDto) {
        db.deletePlaylist(playlistDto)
    }

    override suspend fun getPlaylistByName(name: String) = db.getPlaylistByName(name)

    override suspend fun updatePlaylist(playlist: PlaylistDto) {
        db.updatePlaylist(playlist)
    }
}