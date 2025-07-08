package com.example.mediaplayer.model.local_data_source.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mediaplayer.model.dto.AudioDto
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAudioFile(audio: AudioDto)
    @Query("SELECT * FROM media_Table")
    fun getAllMedia(): Flow<List<AudioDto>>
    @Delete
    suspend fun deleteMediaFile(audio: AudioDto)
    @Query("SELECT * FROM media_Table WHERE id = :audioId LIMIT 1")
    suspend fun getMediaById(audioId: Long): AudioDto?

}