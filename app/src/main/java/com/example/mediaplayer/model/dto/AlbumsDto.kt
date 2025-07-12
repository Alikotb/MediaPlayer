package com.example.mediaplayer.model.dto

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlbumsDto(
    val albumName:String,
    val albumImg: Bitmap?=null,
    val audioFiles:List<AudioDto>
): Parcelable




