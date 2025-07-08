package com.example.mediaplayer.features.albums.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.AlbumsCardBinding
import com.example.mediaplayer.model.dto.AlbumsDto
import com.example.mediaplayer.utils.getAlbumArt

class AlbumAdapter(
    private val albums: List<AlbumsDto>,
    val onAudioItemClick: (AlbumsDto) -> Unit = {}
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    inner class AlbumViewHolder(val binding: AlbumsCardBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AlbumsCardBinding
            .inflate(inflater, parent, false)
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albums[position]
        holder.binding.albumNameTv.text = album.albumName
        holder.binding.albumCountTv.text = "${album.audioFiles.size} song(s)"

        Glide.with(holder.binding.root)
            .asBitmap()
            .load(getAlbumArt(album.audioFiles.first().path))
            .placeholder(R.drawable.outline_music_note_24)
            .into(holder.binding.albumImage)

        holder.binding.albumCardItem.setOnClickListener {
            onAudioItemClick(album)
        }
    }

    override fun getItemCount(): Int = albums.size
}