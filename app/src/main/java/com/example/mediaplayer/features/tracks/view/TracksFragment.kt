package com.example.mediaplayer.features.tracks.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.databinding.FragmentTracksBinding
import com.example.mediaplayer.features.Response
import com.example.mediaplayer.features.home.SplashFragmentDirections
import com.example.mediaplayer.features.tracks.TracksViewModel
import com.example.mediaplayer.model.dto.AlbumsDto
import com.example.mediaplayer.model.dto.AudioDto
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TracksFragment : Fragment() {
    private val viewModel: TracksViewModel by viewModel()
    private var isLoaded = false


    private var _binding: FragmentTracksBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTracksBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isLoaded) {
            viewModel.getAllTracks()
            isLoaded = true

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is Response.Loading -> {

                    }

                    is Response.Success -> {
                        _binding?.trackRecyclerView?.adapter = TracksAdapter(state.data) {
                            navigateToAllAudio(obj = it,state.data)
                        }
                        return@collect
                    }

                    is Response.Error -> {
                        return@collect
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun navigateToAllAudio(obj: AudioDto,list:List<AudioDto>) {
        val action = SplashFragmentDirections.actionSplashFragmentToAudioPlayerFragment(
            audioFile = obj,
            audioList = list.toTypedArray()
        )
        parentFragment?.findNavController()?.navigate(action)


    }
}