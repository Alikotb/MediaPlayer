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
                        _uiState.emit(Response.Success(it))
                    }
            } catch (e: Exception) {
                _uiState.emit(Response.Error(e.message ?: "error"))
            }
        }
    }
}