package com.audio.tunoo.mediaplayer.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.audio.tunoo.mediaplayer.MainActivity
import com.audio.tunoo.mediaplayer.R
import com.audio.tunoo.mediaplayer.model.dto.AudioDto
import com.audio.tunoo.mediaplayer.utils.MediaConstant
import com.audio.tunoo.mediaplayer.utils.getAlbumArt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyMediaService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var mediaSession: MediaSessionCompat? = null

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
    private var isInForeground = false

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, "music").apply {
            isActive = true
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (intent.action) {
                MediaConstant.PREVIOUS -> prev()
                MediaConstant.PLAY_PAUSE -> playPause()
                MediaConstant.NEXT -> next()
                MediaConstant.STOP -> stopPlayback()
            }
        }
        return START_NOT_STICKY
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun play(track: AudioDto) {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(this, getURI(track))
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnCompletionListener {
            next()
        }
        mediaPlayer?.setOnPreparedListener {
            mediaPlayer?.start()
            isPlaying.update { true }
            sendNotification(track)
            updateDuration()
        }
    }

    fun next() {
        if (tracksList.isEmpty()) return
        job?.cancel()
        val currentIndex = tracksList.indexOf(currentTrack.value)
        val nextIndex = if (currentIndex == -1) 0 else (currentIndex + 1) % tracksList.size
        val item = tracksList[nextIndex]
        currentTrack.update { item }
        play(item)
    }

    fun prev() {
        if (tracksList.isEmpty()) return
        job?.cancel()
        val currentIndex = tracksList.indexOf(currentTrack.value)
        val prevIndex = if (currentIndex <= 0) tracksList.size - 1 else currentIndex - 1
        val item = tracksList[prevIndex]
        currentTrack.update { item }
        play(item)
    }

    fun playPause() {
        val player = mediaPlayer
        if (player == null || !isMediaPlayerUsable()) {
            currentTrack.value?.let { play(it) }
            return
        }

        if (player.isPlaying) {
            player.pause()
            isPlaying.update { false }
        } else {
            player.start()
            isPlaying.update { true }
        }

        currentTrack.value?.let { sendNotification(it) }
    }

    fun getURI(track: AudioDto) = track.path.toUri()
    
    private fun updateDuration() {
        job?.cancel()
        job = scope.launch {
            while (mediaPlayer?.isPlaying == true) {
                maxDuration.update { mediaPlayer?.duration?.toFloat() ?: 0f }
                currentDuration.update { mediaPlayer?.currentPosition?.toFloat() ?: 0f }
                delay(1000)
            }
        }
    }

    private fun sendNotification(track: AudioDto) {
        val openPlayerIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            action = "OPEN_PLAYER"
            putExtra("audio", track)
            putParcelableArrayListExtra("audioList", ArrayList(tracksList))
        }

        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openPlayerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)
            .setMediaSession(mediaSession?.sessionToken)

        val notificationBuilder = NotificationCompat.Builder(this, MediaConstant.CHANNEL_ID)
            .setStyle(style)
            .setContentTitle(track.title)
            .setContentText(track.artist)
            .setSmallIcon(R.drawable.music)
            .setContentIntent(contentPendingIntent)
            .setOngoing(isPlaying.value)
            .setAutoCancel(false)
            .addAction(
                R.drawable.baseline_skip_previous_24,
                MediaConstant.PREVIOUS,
                createPrevPendingIntent()
            )
            .addAction(
                if (isPlaying.value) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24,
                MediaConstant.PLAY_PAUSE,
                createPlayPausePendingIntent()
            )
            .addAction(
                R.drawable.baseline_skip_next_24,
                MediaConstant.NEXT,
                createNextPendingIntent()
            )
            .addAction(
                R.drawable.baseline_close_24,
                MediaConstant.STOP,
                createStopPendingIntent()
            )
            .setLargeIcon(getAlbumArt(track.path))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notification = notificationBuilder.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(1, notification)
        }
        isInForeground = true
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopPlayback()
    }

    private fun stopPlayback() {
        mediaPlayer?.run {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        isPlaying.update { false }
        job?.cancel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        isInForeground = false
        stopSelf()
    }

    private fun createStopPendingIntent(): PendingIntent {
        val intent = Intent(this, MyMediaService::class.java).apply { action = MediaConstant.STOP }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createPrevPendingIntent(): PendingIntent {
        val intent = Intent(this, MyMediaService::class.java).apply { action = MediaConstant.PREVIOUS }
        return PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createPlayPausePendingIntent(): PendingIntent {
        val intent = Intent(this, MyMediaService::class.java).apply { action = MediaConstant.PLAY_PAUSE }
        return PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createNextPendingIntent(): PendingIntent {
        val intent = Intent(this, MyMediaService::class.java).apply { action = MediaConstant.NEXT }
        return PendingIntent.getService(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaSession?.release()
        job?.cancel()
    }

    private fun isMediaPlayerUsable(): Boolean {
        return try {
            mediaPlayer?.isPlaying
            true
        } catch (e: Exception) {
            false
        }
    }
}
