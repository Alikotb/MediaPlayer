package com.example.mediaplayer.model.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FolderDto(
    val folderName:String,
    val audioFiles:List<AudioDto>
): Parcelable
