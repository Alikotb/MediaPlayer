package com.example.mediaplayer.features.playlist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.databinding.FragmentPlaylistBinding
import com.example.mediaplayer.features.Response
import com.example.mediaplayer.features.common.createPlaylistDialog
import com.example.mediaplayer.features.common.showCustomSnackbar
import com.example.mediaplayer.features.home.SplashFragmentDirections
import com.example.mediaplayer.features.playlist.PlayListViewModel
import com.example.mediaplayer.model.dto.PlaylistDto
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PlaylistFragment : Fragment() {

    private var binding: FragmentPlaylistBinding? = null
    private val viewModel: PlayListViewModel by inject()
    private var isLoaded = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater,container,false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.snackbarFlow.collect { message ->
                view.post {
                    view.let { safeView ->
                        showCustomSnackbar(safeView, message)
                    }
                }
            }
        }
        binding?.addNewPlaylistRow?.setOnClickListener {
            createPlaylistDialog(requireContext()) { name ->
                 viewModel.addNewList(PlaylistDto(name, listOf()))
            }
        }
        if (!isLoaded) {
            viewModel.getAllPlayList()
            isLoaded = true
        }
        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.uiState.collect { state ->
                when(state){
                    is Response.Error -> {
                        binding?.shimmerLayout?.stopShimmer()
                        binding?.shimmerLayout?.visibility = View.GONE
                        binding?.playlistRecyclerView?.visibility = View.GONE
                        return@collect
                    }
                    is Response.Loading -> {
                       binding?.shimmerLayout?.visibility = View.VISIBLE
                       binding?.shimmerLayout?.startShimmer()
                       binding?.playlistRecyclerView?.visibility = View.GONE
                    }
                    is Response.Success -> {

                       binding?.shimmerLayout?.stopShimmer()
                       binding?.shimmerLayout?.visibility = View.GONE
                       binding?.playlistRecyclerView?.visibility = View.VISIBLE
                        if(state.data.isEmpty()){
                            binding?.lottieAnimationView?.visibility = View.VISIBLE
                        }else{
                            binding?.lottieAnimationView?.visibility = View.GONE

                        }

                        binding?.playlistRecyclerView?.adapter = PlaylistAdapter(requireContext(),state.data, onPlayListClick = {
                            navigateToAllAudio(it)
                        }, onDelete ={
                            viewModel.deletePlaylist(it)
                        } )
                        return@collect
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun navigateToAllAudio(obj: PlaylistDto) {
        val action = SplashFragmentDirections.actionSplashFragmentToAllAudioFragment(
            album = null,
            folder = null,
            playlist = obj

        )
        parentFragment?.findNavController()?.navigate(action)


    }


}