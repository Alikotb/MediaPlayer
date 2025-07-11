package com.example.mediaplayer.features.audio_player

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.mapper.toHistoryDto
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioPlayerViewModel(private val repo: IRepository) : ViewModel() {
    private var mediaPlayer: MediaPlayer? = null
    private val _currentAudio = MutableStateFlow<AudioDto?>(null)
    val currentAudio = _currentAudio.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()
    private val _isFav = MutableStateFlow(false)
    val isFav = _isFav.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress = _progress.asStateFlow()

    private val _duration = MutableStateFlow(0)
    val duration = _duration.asStateFlow()

    private var audioList: List<AudioDto> = emptyList()
    private var updateJob: Job? = null
    fun setFav(value: Boolean) {
        _isFav.value = value
    }
    fun initPlayer(audio: AudioDto, list: List<AudioDto>) {
        audioList = list
        _currentAudio.value = audio
        preparePlayer(audio)
    }
    private fun preparePlayer(audio: AudioDto) {
        addToHistory()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        stopUpdating()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(audio.path)
            prepare()
            start()

            _duration.value = duration
            _isPlaying.value = true

            setOnCompletionListener {
                playNext()
            }
        }
        checkFav(audio)
        startUpdating()
    }


    fun playPauseToggle() {
        if (_isPlaying.value) {
            mediaPlayer?.pause()
            stopUpdating()
        } else {
            mediaPlayer?.start()
            startUpdating()
        }
        _isPlaying.value = !_isPlaying.value
    }

    fun playNext() {
        val index = audioList.indexOf(_currentAudio.value)
        val next = if (index == audioList.lastIndex) audioList.first() else audioList[index + 1]
        _currentAudio.value = next
        preparePlayer(next)
    }

    fun playPrevious() {
        val index = audioList.indexOf(_currentAudio.value)
        val prev = if (index == 0) audioList.last() else audioList[index - 1]
        _currentAudio.value = prev
        preparePlayer(prev)
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        _progress.value = position
    }

    private fun startUpdating() {
        updateJob = viewModelScope.launch {
            while (isActive) {
                mediaPlayer?.currentPosition?.let {
                    _progress.value = it
                }
                delay(1000)
            }
        }
    }

    private fun stopUpdating() {
        updateJob?.cancel()
        updateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdating()
        mediaPlayer?.release()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        stopUpdating()
    }

    fun checkFav(audio: AudioDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _isFav.value = repo.isFav(audio)
        }
    }

    fun addToDatabase(audio: AudioDto){
        viewModelScope.launch (Dispatchers.IO){
            repo.insertAudioFile(audio)
        }
    }

    fun deleteFromDatabase(audio: AudioDto){
        viewModelScope.launch (Dispatchers.IO){
            repo.deleteMediaFile(audio)
        }
    }
    private fun addToHistory(){
        viewModelScope.launch (Dispatchers.IO){
            repo.insertHistoryFile(_currentAudio.value!!.toHistoryDto())
        }
    }

}
