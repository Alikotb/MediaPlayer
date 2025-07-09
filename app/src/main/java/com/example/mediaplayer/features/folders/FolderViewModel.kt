package com.example.mediaplayer.features.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.features.Response
import com.example.mediaplayer.model.dto.FolderDto
import com.example.mediaplayer.model.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FolderViewModel(private val repo: IRepository) : ViewModel() {
    private var _uiState = MutableStateFlow<Response<List<FolderDto>>>(Response.Loading)
    val uiState = _uiState.asStateFlow()

    fun getAllFolders() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.getAllFolders().catch {
                    _uiState.emit(Response.Error(it.message ?: "Error"))
                }.collect {
                    val list = it.sortedWith(compareBy<FolderDto> { item ->
                        val firstChar = item.folderName.firstOrNull() ?: ' '
                        when {
                            firstChar in 'A'..'Z' || firstChar in 'a'..'z' -> 0
                            firstChar in '\u0600'..'\u06FF' -> 1
                            else -> 2
                        }
                    }.thenBy { item ->
                        item.folderName.lowercase()
                    })
                    _uiState.emit(Response.Success(list))
                }

            } catch (e: Exception) {
                _uiState.emit(Response.Error(e.message ?: "Error"))
            }
        }
    }
}