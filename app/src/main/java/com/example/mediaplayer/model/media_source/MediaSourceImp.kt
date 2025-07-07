package com.example.mediaplayer.model.media_source

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import com.example.mediaplayer.model.dto.AlbumsDto
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.FolderDtro
import java.io.File

class MediaSourceImp(val context: Context): IMediaSource {

    private fun loadAllAudio(): List<AudioDto> {
        val musicList = mutableListOf<AudioDto>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val cursor = context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val sizeCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dateCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val albumIdCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (it.moveToNext()) {
                val path = it.getString(pathCol)

                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(path)
                val art = retriever.embeddedPicture
                val albumArt = art?.let { bytes ->
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
                retriever.release()

                val music = AudioDto(
                    id = it.getLong(idCol),
                    title = it.getString(titleCol),
                    artist = it.getString(artistCol),
                    album = it.getString(albumCol),
                    duration = it.getLong(durationCol),
                    path = path,
                    size = it.getLong(sizeCol),
                    dateAdded = it.getLong(dateCol),
                    albumId = it.getLong(albumIdCol),
                    albumArt = albumArt
                )
                musicList.add(music)
            }
        }

        return musicList
    }


    override fun getAllFolders(): List<FolderDtro> {
        val folderMap = mutableMapOf<String, MutableList<AudioDto>>()

        for (audio in loadAllAudio()) {
            val folderName = File(audio.path).parentFile?.name ?: "Unknown"
            folderMap.getOrPut(folderName) { mutableListOf() }.add(audio)
        }

        return folderMap.map { (folderName, audios) ->
            FolderDtro(folderName, audios)
        }
    }


    override  fun getAllAlbums(): List<AlbumsDto> {
        val albumMap = mutableMapOf<String, MutableList<AudioDto>>()

        for (audio in loadAllAudio()) {
            val albumName = audio.album ?: "Unknown Album"
            albumMap.getOrPut(albumName) { mutableListOf() }.add(audio)
        }

        return albumMap.map { (albumName, audios) ->
            val albumImg = audios.firstOrNull { it.albumArt != null }?.albumArt
            AlbumsDto(
                albumName = albumName,
                albumImg = albumImg,
                audioFiles = audios
            )
        }
    }



   override fun getAllMusic(): List<AudioDto> {
        return loadAllAudio()
    }

}