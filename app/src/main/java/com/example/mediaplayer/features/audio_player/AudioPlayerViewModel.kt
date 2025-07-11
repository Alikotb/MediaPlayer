package com.example.mediaplayer.features.audio_player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.mapper.toHistoryDto
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioPlayerViewModel(private val repo: IRepository) : ViewModel() {
    private val _isFav = MutableStateFlow(false)
    val isFav = _isFav.asStateFlow()
    fun setFav(value: Boolean) {
        _isFav.value = value
    }
    fun checkFava(audio: AudioDto){
        viewModelScope.launch {

                viewModelScope.launch(Dispatchers.IO) {
                    _isFav.value = repo.isFav(audio)
                }

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
     fun addToHistory(audio: AudioDto){
        viewModelScope.launch (Dispatchers.IO){
            repo.insertHistoryFile(audio.toHistoryDto())
        }
    }

}
