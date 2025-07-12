package com.example.mediaplayer.model.dto

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "playlist_table")
@Parcelize
data class PlaylistDto(
    @PrimaryKey
    val playlistName : String,
    val audioList : List<AudioDto>
): Parcelable



class PlaylistConverter {

    @TypeConverter
    fun fromAudioList(value: List<AudioDto>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toAudioList(value: String): List<AudioDto> {
        val listType = object : TypeToken<List<AudioDto>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
