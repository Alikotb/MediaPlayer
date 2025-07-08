package com.example.mediaplayer.features.folders.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.databinding.FragmentFoldersBinding
import com.example.mediaplayer.features.Response
import com.example.mediaplayer.features.folders.FolderViewModel
import com.example.mediaplayer.features.home.SplashFragmentDirections
import com.example.mediaplayer.model.dto.FolderDto
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FoldersFragment : Fragment() {
    private val viewModel: FolderViewModel by viewModel()
    private var isLoaded = false


    private var _binding: FragmentFoldersBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoldersBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isLoaded) {
            viewModel.getAllFolders()
            isLoaded = true
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when(state){
                    is Response.Error -> {
                        return@collect
                    }
                    Response.Loading -> {

                    }
                    is Response.Success -> {
                        _binding?.folderRecyclerView?.adapter = FolderAdapter(state.data){
                            navigateToAllAudio(it)
                        }
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

    fun navigateToAllAudio(obj: FolderDto) {
        val action = SplashFragmentDirections.actionSplashFragmentToAllAudioFragment(
            album = null,
            folder = obj
        )
        parentFragment?.findNavController()?.navigate(action)


    }
}