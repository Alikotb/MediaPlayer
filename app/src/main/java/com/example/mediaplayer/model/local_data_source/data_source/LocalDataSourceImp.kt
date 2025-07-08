package com.example.mediaplayer.model.local_data_source.data_source

import com.example.mediaplayer.model.dto.AudioDto
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
}