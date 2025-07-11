package com.example.mediaplayer.features.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentSearchBinding
import com.example.mediaplayer.features.Response
import com.example.mediaplayer.features.tracks.view.TracksAdapter
import com.example.mediaplayer.model.dto.AudioDto
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by inject()
    private var searchText = ""
    private var binding: FragmentSearchBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchView = binding!!.searchView
        binding?.backBtn?.setOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchInTracks(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.searchInTracks(it)
                }
                return true
            }
        })

        setSearchViewConfig(searchView)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchInTracks(searchText)
            viewModel.uiState.collect { state ->
                when (state) {
                    is Response.Loading -> {
                        binding?.shimmerLayout?.visibility = View.VISIBLE
                        binding?.shimmerLayout?.startShimmer()
                        binding?.searchRecyclerView?.visibility = View.GONE
                    }

                    is Response.Success -> {
                        binding?.shimmerLayout?.stopShimmer()
                        binding?.shimmerLayout?.visibility = View.GONE
                        binding?.searchRecyclerView?.visibility = View.VISIBLE
                        if(state.data.isEmpty()){
                            binding?.lottieAnimationView?.visibility = View.VISIBLE
                        }else{
                            binding?.lottieAnimationView?.visibility = View.GONE

                        }
                        binding?.searchRecyclerView?.adapter = TracksAdapter(state.data) {
                           navigateToAudioPlayer(obj = it , list = state.data)
                        }
                        return@collect
                    }

                    is Response.Error -> {

                        binding?.shimmerLayout?.stopShimmer()
                        binding?.shimmerLayout?.visibility = View.GONE
                        binding?.searchRecyclerView?.visibility = View.GONE

                        return@collect
                    }
                }
            }
        }

    }
    private fun navigateToAudioPlayer(obj: AudioDto,list:List<AudioDto>) {
        val action = SearchFragmentDirections.actionSearchFragmentToAudioPlayerFragment(
            audioFile = obj,
            audioList = list.toTypedArray()
        )
        parentFragment?.findNavController()?.navigate(action)
    }
    private fun setSearchViewConfig(searchView: SearchView) {
        searchView.setIconifiedByDefault(false)
        searchView.isIconified = false
        val context = requireContext()

        val searchEditText = searchView.findViewById<EditText>(
            androidx.appcompat.R.id.search_src_text
        )
        val searchIcon = searchView.findViewById<ImageView>(
            androidx.appcompat.R.id.search_mag_icon
        )
        val closeIcon = searchView.findViewById<ImageView>(
            androidx.appcompat.R.id.search_close_btn
        )
        searchView.queryHint = "Search here..."
        searchView.isFocusable = true
        searchView.isFocusableInTouchMode = true
        searchView.isClickable = true
        val textColor = ContextCompat.getColor(context, R.color.search_text)
        val hintColor = ContextCompat.getColor(context, R.color.search_hint)
        val iconColor = ContextCompat.getColor(context, R.color.search_icon)

        searchEditText.setTextColor(textColor)
        searchEditText.setHintTextColor(hintColor)

        searchIcon.setColorFilter(iconColor)
        closeIcon.setColorFilter(iconColor)
    }
}