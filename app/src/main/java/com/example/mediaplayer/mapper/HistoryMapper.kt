package com.example.mediaplayer.mapper

import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.HistoryDto


fun HistoryDto.toAudioDto(): AudioDto {
    return AudioDto(
        id = id,
        title = title,
        artist = artist,
        duration = duration,
        album = null,
        path = path,
        size = 0,
        dateAdded = 0
    )
}

