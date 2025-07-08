package com.example.mediaplayer.utils

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import android.widget.TextView

@SuppressLint("DefaultLocale")
fun Long.toMinutesAndSeconds(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

private val uiHandler = Handler(Looper.getMainLooper())

fun startUpdating(
    player: MediaPlayer,
    seekBar: SeekBar,
    currentTv: TextView
) {
    seekBar.max = player.duration

    fun update() {
        try {
            if (player.isPlaying || player.currentPosition >= 0) {
                val pos = player.currentPosition
                seekBar.progress = pos
                currentTv.text = pos.toLong().toMinutesAndSeconds()
                uiHandler.postDelayed({ update() }, 1000)
            }
        } catch (e: IllegalStateException) {
            // MediaPlayer was released, stop updating
        }
    }

    update()
}

fun stopUpdating() {
    uiHandler.removeCallbacksAndMessages(null)
}

