package com.example.mediaplayer.features.all_audio

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.databinding.FragmentAllAudioBinding
import com.example.mediaplayer.features.common.showCustomSnackbar
import com.example.mediaplayer.features.playlist.PlayListViewModel
import com.example.mediaplayer.features.tracks.view.TracksAdapter
import com.example.mediaplayer.model.dto.AudioDto
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class AllAudioFragment : Fragment() {

    private lateinit var adapter: TracksAdapter
    private val playListViewModel : PlayListViewModel by inject()
    private var binding: FragmentAllAudioBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllAudioBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = AllAudioFragmentArgs.fromBundle(requireArguments())
        val album = args.album
        val folder = args.folder
        val playlist = args.playlist

        viewLifecycleOwner.lifecycleScope.launch {
            playListViewModel.snackbarFlow.collect { message ->
                binding?.root?.let {
                    showCustomSnackbar(it,message)
                }
            }
        }

        val audioFiles: MutableList<AudioDto> = when {
            album != null -> album.audioFiles.toMutableList()
            folder != null -> folder.audioFiles.toMutableList()
            playlist != null -> playlist.audioList.toMutableList()
            else -> mutableListOf()
        }


        val header = when{
            album != null -> album.albumName
            folder != null -> folder.folderName
            playlist !=null -> playlist.playlistName
            else -> ""
        }

        val displayedName = if (header.isNotBlank() && header.length > 20) {
            header.substring(0, 20) + "..."
        } else {
            header
        }

        binding?.titleTv?.text = displayedName
        binding?.backBtn?.setOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }
        if(audioFiles.isEmpty()){
            binding?.lottieAnimationView?.visibility = View.VISIBLE
        }else{
            binding?.lottieAnimationView?.visibility = View.GONE

        }

        adapter = TracksAdapter(
            isPlaylist = playlist != null,
            tracks = audioFiles,
            onAudioItemClick = {
                navigateToAllAudio(it, audioFiles)
            },
            onDelete = {
                playListViewModel.deleteItemFromPlayList(it, playlist?.playlistName!!)
                audioFiles.remove(it)
                if(audioFiles.isEmpty()){
                    binding?.lottieAnimationView?.visibility = View.VISIBLE
                }else{
                    binding?.lottieAnimationView?.visibility = View.GONE

                }
                adapter.notifyDataSetChanged()
            }
        )

        binding?.allAudioRecyclerView?.adapter = adapter


    }

    fun navigateToAllAudio(obj: AudioDto,list:List<AudioDto>) {
        val action = AllAudioFragmentDirections.actionAllAudioFragmentToAudioPlayerFragment(
            audioFile = obj,
            audioList = list.toTypedArray()
        )
        parentFragment?.findNavController()?.navigate(action)
    }
}