package com.example.mediaplayer.model.repository

import com.example.mediaplayer.model.dto.AlbumsDto
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.FolderDto
import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend fun getAllMusic(): Flow<List<AudioDto>>
    suspend fun getAllFolders():Flow<List<FolderDto>>
    suspend fun getAllAlbums(): Flow<List<AlbumsDto>>

    suspend fun insertAudioFile(audio: AudioDto)
    fun getAllMedia(): Flow<List<AudioDto>>
    suspend fun deleteMediaFile(audio: AudioDto)

    suspend fun isFav(audio: AudioDto): Boolean
}