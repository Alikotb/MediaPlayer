package com.example.mediaplayer.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever

fun getAlbumArt(path: String): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val art = retriever.embeddedPicture
        retriever.release()
        art?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    } catch (e: Exception) {
        null
    }
}
