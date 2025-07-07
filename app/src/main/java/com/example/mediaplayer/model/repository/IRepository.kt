package com.example.mediaplayer.model.repository

import com.example.mediaplayer.model.dto.AlbumsDto
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.FolderDtro
import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend fun getAllMusic(): Flow<List<AudioDto>>
    suspend fun getAllFolders():Flow<List<FolderDtro>>
    suspend fun getAllAlbums(): Flow<List<AlbumsDto>>
}