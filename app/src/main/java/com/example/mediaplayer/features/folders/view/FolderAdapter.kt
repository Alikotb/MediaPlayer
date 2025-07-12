package com.example.mediaplayer.features.folders.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FolderCardBinding
import com.example.mediaplayer.model.dto.FolderDto
import com.example.mediaplayer.utils.convertNumbersToArabic

class FolderAdapter(
    private val context: Context,
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.binding.folderNameTv.text = folder.folderName
        holder.binding.folderCountTv.text = "${folder.audioFiles.size} ".convertNumbersToArabic()+ context.getString(R.string.song_s)
        holder.binding.folderCardItem.setOnClickListener {
            onAudioItemClick(folder)
        }
    }

    override fun getItemCount(): Int = folders.size
}