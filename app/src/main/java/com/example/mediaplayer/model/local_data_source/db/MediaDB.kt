package com.example.mediaplayer.model.local_data_source.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.local_data_source.dao.MediaDao

@Database(entities = [AudioDto::class], version = 1)
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