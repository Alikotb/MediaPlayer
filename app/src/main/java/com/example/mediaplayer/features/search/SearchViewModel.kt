package com.example.mediaplayer.features.search

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

class SearchViewModel(private val repo: IRepository): ViewModel() {
    private val _uiState = MutableStateFlow<Response<List<AudioDto>>>(Response.Loading)
    val uiState = _uiState.asStateFlow()

    fun searchInTracks(searchQuery: String){
        viewModelScope.launch (Dispatchers.IO){
            repo.getAllMusic().catch {
                _uiState.emit(Response.Error(it.message?:"Error"))
            }.collect {
                val list = if(searchQuery.isNotBlank()){
                 it.filter {
                    it.title.contains(searchQuery,ignoreCase = true)||it.artist!!.contains(searchQuery,ignoreCase = true)
                }}
                else {
                    it
                }

                _uiState.emit(Response.Success(list))
            }
        }
    }
}