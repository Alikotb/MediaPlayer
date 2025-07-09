package com.example.mediaplayer.model.dto

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "history_Table")
@Parcelize
data class HistoryDto(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String?,
    val duration: Long,
    val path: String,
    val date: Long
) : Parcelable
