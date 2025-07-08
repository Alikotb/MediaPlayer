package com.example.mediaplayer.features.audio_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentAudioPlayerBinding
import com.example.mediaplayer.utils.getAlbumArt
import com.example.mediaplayer.utils.toMinutesAndSeconds
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AudioPlayerFragment : Fragment() {
    private var isFav = true
    private val viewModel: AudioPlayerViewModel by inject()
    private var binding: FragmentAudioPlayerBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = AudioPlayerFragmentArgs.fromBundle(requireArguments())
        viewModel.initPlayer(args.audioFile!!, args.audioList.toList())
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentAudio.collect { audio ->
                audio?.let {
                    binding?.trackNameAudioPlayerTv?.apply {
                        text = audio.title
                        isSelected = true
                    }
                    binding?.trackArtistAudioPlayerTv?.text = it.artist ?: "غير معروف"

                    Glide.with(requireContext())
                        .asBitmap()
                        .load(getAlbumArt(it.path))
                        .placeholder(R.drawable.cylinder)
                        .into(binding?.imageAudio ?: return@collect)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isPlaying.collect { playing ->
                val icon =
                    if (playing) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                binding?.playPauseBtn?.setImageResource(icon)

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.progress.collect { progress ->
                binding?.seekBar?.progress = progress
                binding?.startTimeTv?.text = progress.toLong().toMinutesAndSeconds()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.duration.collect { duration ->
                binding?.seekBar?.max = duration
                binding?.totalTimeTv?.text = duration.toLong().toMinutesAndSeconds()
            }
        }

        binding?.playPauseBtn?.setOnClickListener {
            viewModel.playPauseToggle()
        }

        binding?.favBtn?.setOnClickListener {
            isFav = !isFav
            val newIcon =
                if (isFav) R.drawable.baseline_favorite_24 else R.drawable.outline_favorite_24
            binding?.favBtn?.setImageResource(newIcon)
        }

        binding?.nextBtn?.setOnClickListener {
            viewModel.playNext()
        }

        binding?.previousBtn?.setOnClickListener {
            viewModel.playPrevious()
        }

        binding?.seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) viewModel.seekTo(progress)
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
        binding?.backBtn?.setOnClickListener {
            viewModel.stop()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        viewModel.stop()
        binding = null
        super.onDestroyView()
    }
}