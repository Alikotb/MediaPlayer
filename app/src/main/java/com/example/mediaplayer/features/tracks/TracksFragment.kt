package com.example.mediaplayer.features.tracks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mediaplayer.databinding.FragmentTracksBinding
import com.example.mediaplayer.model.media_source.IMediaSource
import com.example.mediaplayer.model.media_source.MediaSourceImp
import org.koin.android.ext.android.inject
import kotlin.getValue

class TracksFragment : Fragment() {
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
        val source: IMediaSource by inject()
        val list = source.getAllMusic()
        val album =source.getAllAlbums()
        val folders = source.getAllFolders()

        Log.d("ali", "onViewCreated:all music " +
                "title: ${list[55].title}" +
                "=== path : ${list[55].path}" )

        Log.d("ali", "onViewCreated:all folders" +
                " title: ${folders[5].folderName}" +
                "=== path : ${folders[1].audioFiles.first().title}" )


        Log.d("ali", "onViewCreated:all album " +
                "title: ${album[4].albumName}" +
                "=== path : ${album[2].audioFiles.first().title} == image${album[1].albumImg}" )
    }


}