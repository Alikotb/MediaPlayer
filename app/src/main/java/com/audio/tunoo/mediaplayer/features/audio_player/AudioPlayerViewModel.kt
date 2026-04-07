package com.audio.tunoo.mediaplayer.features.audio_player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audio.tunoo.mediaplayer.mapper.toHistoryDto
import com.audio.tunoo.mediaplayer.model.dto.AudioDto
import com.audio.tunoo.mediaplayer.model.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AudioPlayerViewModel(private val repo: IRepository) : ViewModel() {
    private val _snackbarFlow = MutableSharedFlow<Int>()
    val snackbarFlow = _snackbarFlow

    private val _isFav = MutableStateFlow(false)
    val isFav = _isFav.asStateFlow()
    fun setFav(value: Boolean) {
        _isFav.value = value
    }

    fun checkFava(audio: AudioDto) {
        viewModelScope.launch {

            viewModelScope.launch(Dispatchers.IO) {
                _isFav.value = repo.isFav(audio)
            }

        }
    }

    fun addToDatabase(audio: AudioDto) {
        viewModelScope.launch {
            val isAlreadyFav = withContext(Dispatchers.IO) {
                repo.isFav(audio)
            }

            if (isAlreadyFav) {
                _snackbarFlow.emit(com.audio.tunoo.mediaplayer.R.string.already_exists)
            } else {
                withContext(Dispatchers.IO) {
                    repo.insertAudioFile(audio)
                }
                _snackbarFlow.emit(com.audio.tunoo.mediaplayer.R.string.added_successfully)
            }
        }
    }


    fun deleteFromDatabase(audio: AudioDto) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteMediaFile(audio)
            _snackbarFlow.emit(com.audio.tunoo.mediaplayer.R.string.deleted_successfully)

        }
    }

    fun addToHistory(audio: AudioDto) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertHistoryFile(audio.toHistoryDto())
        }
    }

}
