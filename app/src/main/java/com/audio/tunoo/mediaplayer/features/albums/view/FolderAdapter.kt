package com.audio.tunoo.mediaplayer.features.albums.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.audio.tunoo.mediaplayer.R
import com.audio.tunoo.mediaplayer.databinding.AlbumsCardBinding
import com.audio.tunoo.mediaplayer.model.dto.AlbumsDto
import com.audio.tunoo.mediaplayer.utils.convertNumbersToArabic
import com.audio.tunoo.mediaplayer.utils.getAlbumArt
import com.bumptech.glide.Glide

class AlbumAdapter(
    private val context: Context,
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albums[position]
        holder.binding.albumNameTv.text = album.albumName
        holder.binding.albumCountTv.text = "${album.audioFiles.size} ".convertNumbersToArabic()+ context.getString(R.string.song_s)

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