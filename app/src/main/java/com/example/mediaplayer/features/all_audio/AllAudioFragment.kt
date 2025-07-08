package com.example.mediaplayer.features.all_audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.databinding.FragmentAllAudioBinding
import com.example.mediaplayer.features.tracks.view.TracksAdapter
import com.example.mediaplayer.model.dto.AudioDto


class AllAudioFragment : Fragment() {

    private var binding: FragmentAllAudioBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllAudioBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = AllAudioFragmentArgs.fromBundle(requireArguments())
        val album = args.album
        val folder = args.folder

        val audioFiles = when {
            album != null -> album.audioFiles
            folder != null -> folder.audioFiles
            else -> emptyList()
        }

        binding?.allAudioRecyclerView?.adapter = TracksAdapter(audioFiles){
            navigateToAllAudio(it,audioFiles)
        }

    }

    fun navigateToAllAudio(obj: AudioDto,list:List<AudioDto>) {
        val action = AllAudioFragmentDirections.actionAllAudioFragmentToAudioPlayerFragment(
            audioFile = obj,
            audioList = list.toTypedArray()
        )
        parentFragment?.findNavController()?.navigate(action)
    }
}