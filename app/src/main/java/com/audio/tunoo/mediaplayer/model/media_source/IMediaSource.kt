package com.audio.tunoo.mediaplayer.model.media_source

import com.audio.tunoo.mediaplayer.model.dto.AlbumsDto
import com.audio.tunoo.mediaplayer.model.dto.AudioDto
import com.audio.tunoo.mediaplayer.model.dto.FolderDto

interface IMediaSource {
    fun getAllFolders(): List<FolderDto>
    fun getAllAlbums(): List<AlbumsDto>
    fun getAllMusic(): List<AudioDto>
}