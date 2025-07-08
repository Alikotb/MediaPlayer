package com.example.mediaplayer.model.dto

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "media_Table")
@Parcelize
data class AudioDto(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String?,
    val album: String?,
    val duration: Long,
    val path: String,
    val size: Long,
    val dateAdded: Long,
    val albumId: Long? = null,
    val isFavorite: Boolean = false
) : Parcelable

