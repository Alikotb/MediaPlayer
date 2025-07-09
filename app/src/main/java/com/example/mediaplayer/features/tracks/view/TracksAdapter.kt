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
        holder.binding.trackNameTv.text = track.title
        holder.binding.trackArtistNameTv.text = track.artist
        holder.binding.trackCardItem.setOnClickListener {
            onAudioItemClick(track)
        }
    }

    override fun getItemCount(): Int = tracks.size
}