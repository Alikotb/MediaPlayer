package com.example.mediaplayer.features.audio_player.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.databinding.PlaylistBottomSheetCardBinding

class PlayListAdapter(
    private val playlists: MutableList<String>,
    private val onPlaylistClick: (String) -> Unit
) : RecyclerView.Adapter<PlayListAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(val binding: PlaylistBottomSheetCardBinding
    ) : RecyclerView.ViewHolder(binding.root)
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<String>) {
        playlists.clear()
        playlists.addAll(newList)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaylistBottomSheetCardBinding
            .inflate(inflater, parent, false)
        return PlaylistViewHolder(binding)
    }
    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.binding.playlistNameCardTv.text = playlist
        holder.binding.addToPlaylistRow.setOnClickListener {
            onPlaylistClick(playlist)
        }
    }
    override fun getItemCount(): Int = playlists.size
}