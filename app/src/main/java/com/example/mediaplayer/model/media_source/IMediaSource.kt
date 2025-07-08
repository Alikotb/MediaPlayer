package com.example.mediaplayer.model.media_source

import com.example.mediaplayer.model.dto.AlbumsDto
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.FolderDto

interface IMediaSource {
    fun getAllFolders(): List<FolderDto>
    fun getAllAlbums(): List<AlbumsDto>
    fun getAllMusic(): List<AudioDto>
}