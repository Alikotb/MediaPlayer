package com.example.mediaplayer.features.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.databinding.FragmentFavoriteBinding
import com.example.mediaplayer.features.Response
import com.example.mediaplayer.features.tracks.view.TracksAdapter
import com.example.mediaplayer.model.dto.AudioDto
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class FavoriteFragment : Fragment() {

    private val viewModel: FavoriteViewModel by inject()
    private var binding : FragmentFavoriteBinding? = null
    private var isLoaded = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.backBtn?.setOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }
        if (!isLoaded) {
            viewModel.getAllFavMedia()
            isLoaded = true

        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is Response.Loading -> {
                        binding?.shimmerLayout?.visibility = View.VISIBLE
                        binding?.shimmerLayout?.startShimmer()
                        binding?.favRecyclerView?.visibility = View.GONE
                    }
                    is Response.Success -> {
                        binding?.shimmerLayout?.stopShimmer()
                        binding?.shimmerLayout?.visibility = View.GONE
                        binding?.favRecyclerView?.visibility = View.VISIBLE
                        if(state.data.isEmpty()){
                            binding?.lottieAnimationView?.visibility = View.VISIBLE
                        }else{
                            binding?.lottieAnimationView?.visibility = View.GONE

                        }
                        binding?.favRecyclerView?.adapter = TracksAdapter(
                             false,
                            state.data
                            , onAudioItemClick = {
                                navigateToAudioPlayer(obj = it, state.data)
                            })
                        return@collect
                    }

                    is Response.Error -> {
                        binding?.shimmerLayout?.stopShimmer()
                        binding?.shimmerLayout?.visibility = View.GONE
                        binding?.favRecyclerView?.visibility = View.GONE
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

    fun navigateToAudioPlayer(obj: AudioDto,list:List<AudioDto>) {
        val action = FavoriteFragmentDirections.actionFavoriteFragmentToAudioPlayerFragment(
            audioFile = obj,
            audioList = list.toTypedArray()
        )
        parentFragment?.findNavController()?.navigate(action)
    }

}