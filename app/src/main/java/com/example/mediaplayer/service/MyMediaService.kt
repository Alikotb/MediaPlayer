package com.example.mediaplayer.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.utils.MediaConstant
import com.example.mediaplayer.utils.getAlbumArt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyMediaService : Service() {
    private var mediaPlayer = MediaPlayer()
    inner class MediaBinder : Binder() {
        fun getService() = this@MyMediaService
        fun setList(list: List<AudioDto>, audioDto: AudioDto) {
            this@MyMediaService.tracksList = list.toMutableList()
            this@MyMediaService.currentTrack.value = audioDto
        }

    }
    val binder = MediaBinder()
    var currentTrack = MutableStateFlow<AudioDto?>(null)
    private var tracksList = mutableListOf<AudioDto>()

    val isPlaying = MutableStateFlow(false)
    val maxDuration = MutableStateFlow(0f)
    val currentDuration = MutableStateFlow(0f)
    private val scope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dummyNotification = NotificationCompat.Builder(this, MediaConstant.CHANNEL_ID)
                .setContentTitle("Preparing audio...")
                .setContentText("Please wait")
                .setSmallIcon(R.drawable.music)
                .build()
            startForeground(1, dummyNotification)
        }
        intent?.let {
            when (intent.action) {
                MediaConstant.PREVIOUS -> {
                    prev()
                }
                MediaConstant.PLAY_PAUSE -> {
                    playPause()
                }
                MediaConstant.NEXT -> {
                    next()
                }
            }
        }

        return START_STICKY
    }
    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }


    fun play(track: AudioDto) {

        mediaPlayer.reset()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(this, getURI(track))
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnCompletionListener {
            next()
        }
        mediaPlayer.setOnPreparedListener {

            mediaPlayer.start()
            sendNotification(track)
            updateDuration()
        }
    }

    fun next() {
        job?.cancel()
        val item = tracksList.get(
            tracksList.indexOf(currentTrack.value)
                .plus(1).mod(tracksList.size)
        )
        currentTrack.update { item }
        play(currentTrack.value!!)
    }
    fun prev() {
        job?.cancel()
        val index = (tracksList.indexOf(currentTrack.value!!))
        val prevItem = if (index <= 0) tracksList.size.minus(1) else index.minus(1)
        val item = tracksList.get(prevItem)
        currentTrack.update { item }
        play(currentTrack.value!!)
    }
    fun playPause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
        sendNotification(currentTrack.value!!)
    }
    fun getURI(track: AudioDto) = track.path.toUri()
    private fun updateDuration() {
        job = scope.launch {
            if (mediaPlayer.isPlaying.not()) return@launch
            maxDuration.update { mediaPlayer.duration.toFloat() }
            while (true) {
                currentDuration.update { mediaPlayer.currentPosition.toFloat() }
                delay(1000)
            }
        }
    }
    private fun sendNotification(track: AudioDto) {

        val openPlayerIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            action = "OPEN_PLAYER"
            putExtra("audio", track)
        }

        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openPlayerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val session = MediaSessionCompat(this, "music")
        isPlaying.update { mediaPlayer.isPlaying }
        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)
            .setMediaSession(session.sessionToken)

        val notification = NotificationCompat.Builder(this, MediaConstant.CHANNEL_ID)
            .setStyle(style)
            .setContentTitle(track.title)
            .setContentText(track.artist)
            .setSmallIcon(R.drawable.music)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(false)
            .addAction(
                R.drawable.baseline_skip_previous_24,
                MediaConstant.PREVIOUS,
                createPrevPendingIntent()
            )
            .addAction(
                if (mediaPlayer.isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24,
                MediaConstant.PLAY_PAUSE,
                createPlayPausePendingIntent()
            )
            .addAction(
                R.drawable.baseline_skip_next_24,
                MediaConstant.NEXT,
                createNextPendingIntent()
            )
            .setLargeIcon(getAlbumArt(track.path))
            .build()
        startForeground(1, notification)
    }
    fun createPrevPendingIntent(): PendingIntent {
        val intent = Intent(this, MyMediaService::class.java).apply {
            action = MediaConstant.PREVIOUS
        }
        return PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    fun createPlayPausePendingIntent(): PendingIntent {
        val intent = Intent(this, MyMediaService::class.java).apply {
            action = MediaConstant.PLAY_PAUSE
        }
        return PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    fun createNextPendingIntent(): PendingIntent {
        val intent = Intent(this, MyMediaService::class.java).apply {
            action = MediaConstant.NEXT
        }
        return PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        job?.cancel()
    }
}
