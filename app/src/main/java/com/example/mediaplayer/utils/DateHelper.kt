package com.example.mediaplayer.utils

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun Long.toMinutesAndSeconds(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}



