package com.example.mediaplayer.mapper

import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.HistoryDto
import java.util.Date
fun AudioDto.toHistoryDto(): HistoryDto {
    return HistoryDto(
        id = id,
        title = title,
        artist = artist,
        duration = duration,
        path = path,
        date = Date().time
    )
}