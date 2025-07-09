package com.example.mediaplayer.model.local_data_source.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.HistoryDto
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


    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertHistoryFile(historyDto: HistoryDto)
    @Query("SELECT * FROM history_Table")
    fun getAllHistory(): Flow<List<HistoryDto>>


}