package com.example.mediaplayer.model.dto

import android.graphics.Bitmap

data class AudioDto(
    val id: Long,
    val title: String,
    val artist: String?,
    val album: String?,
    val duration: Long,
    val path: String,
    val size: Long,
    val dateAdded: Long,
    val albumId: Long? = null,
    val albumArt: Bitmap? = null
)
