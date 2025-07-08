package com.example.mediaplayer.features.folders.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.databinding.FolderCardBinding
import com.example.mediaplayer.model.dto.FolderDto

class FolderAdapter(
    private val folders: List<FolderDto>,
    val onAudioItemClick: (FolderDto) -> Unit = {}
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    inner class FolderViewHolder(val binding: FolderCardBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FolderCardBinding
            .inflate(inflater, parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.binding.folderNameTv.text = folder.folderName
        holder.binding.folderCountTv.text = "${folder.audioFiles.size} song(s)"
        holder.binding.folderCardItem.setOnClickListener {
            onAudioItemClick(folder)
        }
    }

    override fun getItemCount(): Int = folders.size
}