package com.example.mediaplayer.features.tracks.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.databinding.TrackCardBinding
import com.example.mediaplayer.model.dto.AudioDto
class TracksAdapter(
    private val tracks: List<AudioDto>,
    val onAudioItemClick: (AudioDto) -> Unit = {}
) : RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(val binding: TrackCardBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TrackCardBinding
            .inflate(inflater, parent, false)
        return TrackViewHolder(binding)
    }
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]

        val artist = track.artist?.takeIf { it.isNotBlank() } ?: "Unknown Artist"
        val album = track.album?.takeIf { it.isNotBlank() } ?: "Unknown Album"

        val fullInfo = "$artist - $album"
        val displayedInfo = if (fullInfo.length > 40) {
            fullInfo.take(30) + "..."
        } else {
            fullInfo
        }

        val displayedInfo2 = if (track.title.length > 37) {
            track.title.take(35) + "..."
        } else {
            track.title
        }

        holder.binding.trackNameTv.text = displayedInfo2
        holder.binding.trackArtistNameTv.text = displayedInfo

        holder.binding.trackCardItem.setOnClickListener {
            onAudioItemClick(track)
        }
    }


    override fun getItemCount(): Int = tracks.size
}