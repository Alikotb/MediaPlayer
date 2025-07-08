package com.example.mediaplayer.features.favorite

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

class FavoriteViewModel(private val repo: IRepository): ViewModel() {
    private val _uiState = MutableStateFlow<Response<List<AudioDto>>>(Response.Loading)
    val uiState = _uiState.asStateFlow()
    fun getAllFavMedia(){
        viewModelScope.launch (Dispatchers.IO) {
            repo.getAllMedia().catch {
                _uiState.emit(Response.Error(it.message?:"Error"))

            }.collect {
                _uiState.emit(Response.Success(it))
            }
        }
    }
}