package com.example.mediaplayer.features.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.features.Response
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.PlaylistDto
import com.example.mediaplayer.model.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PlayListViewModel(private val repo: IRepository) : ViewModel() {
    private val _snackbarFlow = MutableSharedFlow<String>()
    val snackbarFlow = _snackbarFlow

    private var _uiState = MutableStateFlow<Response<List<PlaylistDto>>>(Response.Loading)
    val uiState = _uiState.asStateFlow()
    private var _namesState = MutableStateFlow<Response<List<String>>>(Response.Loading)
    val namesState = _namesState.asStateFlow()
    private var searchList: MutableList<String> = mutableListOf()
    private val mutex = Mutex()
    fun getAllPlayList() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllPlaylists().catch {
                _uiState.emit(Response.Error(it.message ?: "Error"))
            }.collect {
                val list = it.sortedBy {
                    it.playlistName
                }
                _uiState.emit(Response.Success(list))
                synchronized(searchList) {
                    searchList.clear()
                    searchList.addAll(list.map { it.playlistName })
                    _namesState.value = Response.Success(list.map { it.playlistName })
                }
            }
        }
    }

    private suspend fun isPlaylistExists(playlistName: String): Boolean {
        return mutex.withLock {
            searchList.any { it == playlistName }
        }
    }

    fun addNewList(playlistDto: PlaylistDto) {
        viewModelScope.launch(Dispatchers.IO) {
            if (playlistDto.playlistName.isNotBlank()&&playlistDto.playlistName.isNotEmpty()) {
                if (isPlaylistExists(playlistDto.playlistName)) {
                    _snackbarFlow.emit("${playlistDto.playlistName} already existed")

                } else {
                    repo.insertNewPlayList(playlistDto)
                    _snackbarFlow.emit("${playlistDto.playlistName} added successfully")
                    getAllPlayList()
                }
            } else {
                _snackbarFlow.emit("Enter Playlist name")
                return@launch
            }
        }
    }

    fun deletePlaylist(playlistDto: PlaylistDto) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deletePlaylist(playlistDto)
            getAllPlayList()
            _snackbarFlow.emit("${playlistDto.playlistName} deleted successfully")

        }
    }

    fun deleteItemFromPlayList(audioDto: AudioDto, playlistName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repo.getPlaylistByName(playlistName)
            val updatedList = (current?.audioList ?: emptyList()).toMutableList().apply {
                removeAll { it.path == audioDto.path }
            }
            val updatedPlaylist = PlaylistDto(playlistName, updatedList)
            repo.insertNewPlayList(updatedPlaylist)
            getAllPlayList()
            _snackbarFlow.emit("${audioDto.title} deleted successfully")

        }
    }


    fun insertAudioInPlayList(audioDto: AudioDto, playlistName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repo.getPlaylistByName(playlistName)

            val currentList = current?.audioList ?: emptyList()

            val alreadyExists = currentList.any { it.path == audioDto.path }

            if (!alreadyExists) {
                val updatedList = currentList.toMutableList().apply {
                    add(audioDto)
                    _snackbarFlow.emit("${audioDto.title} added successfully")
                }
                val updatedPlaylist = PlaylistDto(playlistName, updatedList)
                repo.insertNewPlayList(updatedPlaylist)
            } else {
                _snackbarFlow.emit("${audioDto.title} already exist")
            }
        }
    }


}