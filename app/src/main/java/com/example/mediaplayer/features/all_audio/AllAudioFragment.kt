package com.example.mediaplayer.features.all_audio

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentAllAudioBinding
import com.example.mediaplayer.features.tracks.view.TracksAdapter


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

        }



    }
}