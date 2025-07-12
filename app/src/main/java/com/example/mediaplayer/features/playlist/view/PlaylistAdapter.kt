package com.example.mediaplayer.features.playlist.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.PlaylistCardBinding
import com.example.mediaplayer.model.dto.PlaylistDto
import com.example.mediaplayer.utils.convertNumbersToArabic
import com.example.mediaplayer.utils.getAlbumArt

class PlaylistAdapter(
    private val  context: Context,
    private val playlists: List<PlaylistDto>,
    val onPlayListClick: (PlaylistDto) -> Unit = {},
    val onDelete: (PlaylistDto) -> Unit = {},
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(val binding: PlaylistCardBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaylistCardBinding
            .inflate(inflater, parent, false)
        return PlaylistViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.binding.folderNameTv.text = playlist.playlistName
        holder.binding.folderCountTv.text = "${playlist.audioList.size}  ".convertNumbersToArabic()+ context.getString(R.string.song_s)
        holder.binding.folderCardItem.setOnClickListener {
            onPlayListClick(playlist)
        }
        holder.binding.deleteBtn.setOnClickListener {
            onDelete(playlist)
        }
        val firstAudio = playlist.audioList.firstOrNull()
        if (firstAudio != null) {
            Glide.with(context)
                .load(getAlbumArt(firstAudio.path))
                .placeholder(R.drawable.outline_queue_music_24)
                .into(holder.binding.icon)
        } else {
            holder.binding.icon.setImageResource(R.drawable.outline_queue_music_24)
        }

    }

    override fun getItemCount(): Int = playlists.size
}