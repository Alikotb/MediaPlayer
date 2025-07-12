package com.example.mediaplayer.features.history


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.features.Response
import com.example.mediaplayer.mapper.toAudioDto
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HistoryViewModel(private val repo : IRepository): ViewModel() {
    private val _uiState = MutableStateFlow<Response<List<AudioDto>>>(Response.Loading)
    val uiState = _uiState.asStateFlow()

    fun getAllHistoryFiles(){
        viewModelScope.launch (Dispatchers.IO){
            repo.getAllHistory().catch {
                _uiState.emit(Response.Error(it.message?:"Error"))
            }.collect {
                val list = it
                    .sortedByDescending { history -> history.date }
                    .take(30).map { it.toAudioDto() }
                _uiState.emit(Response.Success(list))
            }
        }
    }
}
