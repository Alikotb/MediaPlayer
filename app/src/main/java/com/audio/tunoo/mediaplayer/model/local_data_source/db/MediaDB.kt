package com.audio.tunoo.mediaplayer.model.local_data_source.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.audio.tunoo.mediaplayer.model.dto.AudioDto
import com.audio.tunoo.mediaplayer.model.dto.HistoryDto
import com.audio.tunoo.mediaplayer.model.dto.PlaylistConverter
import com.audio.tunoo.mediaplayer.model.dto.PlaylistDto
import com.audio.tunoo.mediaplayer.model.local_data_source.dao.MediaDao

@Database(entities = [AudioDto::class, HistoryDto::class, PlaylistDto::class], version = 3)
@TypeConverters(PlaylistConverter::class)
abstract class MediaDB : RoomDatabase() {
    abstract fun getDao(): MediaDao
    companion object {
        @Volatile
        private var instance: MediaDB? = null
        fun getInstance(context: Context): MediaDB {
            return instance ?: synchronized(this) {
                val INSTANCE = Room.databaseBuilder(context.applicationContext, MediaDB::class.java, "roomdb")
                    .fallbackToDestructiveMigration().build()
                instance = INSTANCE
                INSTANCE
            }
        }
    }
}
