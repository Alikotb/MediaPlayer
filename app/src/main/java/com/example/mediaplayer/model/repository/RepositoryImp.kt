package com.example.mediaplayer.model.repository

import com.example.mediaplayer.model.dto.AlbumsDto
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.FolderDto
import com.example.mediaplayer.model.media_source.IMediaSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RepositoryImp(private val mediaSource: IMediaSource):IRepository{
    override suspend fun getAllMusic(): Flow<List<AudioDto>> {
        return flowOf( mediaSource.getAllMusic())
    }

    override suspend fun getAllFolders(): Flow<List<FolderDto>> {
        return flowOf(mediaSource.getAllFolders())
    }

    override suspend fun getAllAlbums(): Flow<List<AlbumsDto>> {
        return flowOf(mediaSource.getAllAlbums())
    }

}