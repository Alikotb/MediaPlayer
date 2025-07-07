package com.example.mediaplayer.model.media_source

import com.example.mediaplayer.model.dto.AlbumsDto
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.FolderDtro

interface IMediaSource {
    fun getAllFolders(): List<FolderDtro>
    fun getAllAlbums(): List<AlbumsDto>
    fun getAllMusic(): List<AudioDto>
}