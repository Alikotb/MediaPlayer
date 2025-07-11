package com.example.mediaplayer.features.albums.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.databinding.FragmentAlbumsBinding
import com.example.mediaplayer.features.Response
import com.example.mediaplayer.features.albums.AlbumViewModel
import com.example.mediaplayer.features.home.SplashFragmentDirections
import com.example.mediaplayer.model.dto.AlbumsDto
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class AlbumsFragment : Fragment() {
    private val viewModel: AlbumViewModel by inject()
    private var  isLoading = false
    private var binding : FragmentAlbumsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!isLoading){
            viewModel.getAllAlbums()
            isLoading=true
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when(state) {
                    is Response.Error ->{
                        binding?.shimmerLayout?.stopShimmer()
                        binding?.shimmerLayout?.visibility = View.GONE
                        binding?.albumRecyclerView?.visibility = View.GONE
                        return@collect
                    }

                    is Response.Loading -> {
                        binding?.shimmerLayout?.visibility = View.VISIBLE
                        binding?.shimmerLayout?.startShimmer()
                        binding?.albumRecyclerView?.visibility = View.GONE
                    }

                    is Response.Success -> {

                        binding?.shimmerLayout?.stopShimmer()
                        binding?.shimmerLayout?.visibility = View.GONE
                        binding?.albumRecyclerView?.visibility = View.VISIBLE
                        if(state.data.isEmpty()){
                            binding?.lottieAnimationView?.visibility = View.VISIBLE
                        }else{
                            binding?.lottieAnimationView?.visibility = View.GONE

                        }
                        binding?.albumRecyclerView?.adapter = AlbumAdapter(state.data){
                            navigateToAllAudio(obj=it)

                        }
                    }
                }
                }
            }
    }
    fun navigateToAllAudio(obj: AlbumsDto) {
        val action = SplashFragmentDirections.actionSplashFragmentToAllAudioFragment(
            album = obj,
            folder = null
        )
        parentFragment?.findNavController()?.navigate(action)


    }
}