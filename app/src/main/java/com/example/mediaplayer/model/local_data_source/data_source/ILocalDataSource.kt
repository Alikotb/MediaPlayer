package com.example.mediaplayer.model.local_data_source.data_source


import com.example.mediaplayer.model.dto.AudioDto
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    suspend fun insertAudioFile(audio: AudioDto)
    fun getAllMedia(): Flow<List<AudioDto>>
    suspend fun deleteMediaFile(audio: AudioDto)
    suspend fun isFav(audio: AudioDto): Boolean
}