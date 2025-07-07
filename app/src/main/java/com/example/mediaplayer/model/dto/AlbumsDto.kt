package com.example.mediaplayer.model.dto

import android.graphics.Bitmap

data class AlbumsDto(
    val albumName:String,
    val albumImg: Bitmap?=null,
    val audioFiles:List<AudioDto>
)


