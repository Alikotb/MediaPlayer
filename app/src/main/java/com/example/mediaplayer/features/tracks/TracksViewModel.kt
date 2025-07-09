package com.example.mediaplayer.features.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.features.Response
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TracksViewModel(private val repo: IRepository) : ViewModel() {
    private var _uiState = MutableStateFlow<Response<List<AudioDto>>>(Response.Loading)
    val uiState = _uiState.asStateFlow()

    fun getAllTracks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.getAllMusic()
                    .catch {
                        _uiState.emit(Response.Error(it.message ?: "Error"))
                    }
                    .collect {
                        val list = it.sortedWith(compareBy<AudioDto> { item ->
                            val firstChar = item.title.firstOrNull() ?: ' '
                            when {
                                firstChar in 'A'..'Z' || firstChar in 'a'..'z' -> 0
                                firstChar in '\u0600'..'\u06FF' -> 1
                                else -> 2
                            }
                        }.thenBy { item ->
                            item.title.lowercase()
                        })

                        _uiState.emit(Response.Success(list))
                    }

            } catch (e: Exception) {
                _uiState.emit(Response.Error(e.message ?: "error"))
            }
        }
    }
}