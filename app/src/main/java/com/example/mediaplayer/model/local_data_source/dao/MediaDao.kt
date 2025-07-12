package com.example.mediaplayer.model.local_data_source.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.HistoryDto
import com.example.mediaplayer.model.dto.PlaylistDto
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

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertNewPlayList(playlistDto: PlaylistDto)
    @Query("SELECT * FROM playlist_table")
    fun getAllPlaylists(): Flow<List<PlaylistDto>>
    @Delete
    suspend fun deletePlaylist(playlistDto: PlaylistDto)

    @Query("SELECT * FROM playlist_table WHERE playlistName = :name")
    suspend fun getPlaylistByName(name: String): PlaylistDto?

    @Update
    suspend fun updatePlaylist(playlist: PlaylistDto)


}