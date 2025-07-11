package com.example.mediaplayer.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.core.graphics.scale

fun getAlbumArt(path: String, size: Int = 256): Bitmap? {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)
        val art = mmr.embeddedPicture
        return if (art != null) {
            val original = BitmapFactory.decodeByteArray(art, 0, art.size)
            original.scale(size, size)
        } else null
    }

