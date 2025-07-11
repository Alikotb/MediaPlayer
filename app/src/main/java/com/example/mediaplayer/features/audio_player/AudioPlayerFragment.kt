package com.example.mediaplayer.features.audio_player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentAudioPlayerBinding
import com.example.mediaplayer.service.MyMediaService
import com.example.mediaplayer.utils.getAlbumArt
import com.example.mediaplayer.utils.toMinutesAndSeconds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class AudioPlayerFragment : Fragment() {
    private val viewModel: AudioPlayerViewModel by inject()
    private var binding: FragmentAudioPlayerBinding? = null
    private var isPlaying = MutableStateFlow(false)
    private var service: MyMediaService? = null
    private var isBound = false
    val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val mediaBinder = binder as MyMediaService.MediaBinder
            service = mediaBinder.getService()
            val args = AudioPlayerFragmentArgs.fromBundle(requireArguments())
            service?.binder?.setList(args.audioList.toList(), args.audioFile!!)
            service?.play(args.audioFile!!)
            isBound = true
            lifecycleScope.launch {
                service?.isPlaying?.collectLatest {
                    isPlaying.value = it
                }
            }
        }


        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            isBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = AudioPlayerFragmentArgs.fromBundle(requireArguments())
        viewModel.initPlayer(args.audioFile!!, args.audioList.toList())

        val serviceIntent = Intent(requireContext(), MyMediaService::class.java)
        requireActivity().startForegroundService(serviceIntent)
        requireActivity().bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)




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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFav.collect { fav ->
                val newIcon = if (fav) {
                    R.drawable.baseline_favorite_24
                } else {
                    R.drawable.outline_favorite_24
                }
                binding?.favBtn?.setImageResource(newIcon)
            }
        }

        binding?.favBtn?.setOnClickListener {
            viewModel.isFav.value.let { fav ->
                if (fav) {
                    viewModel.deleteFromDatabase(args.audioFile)
                } else {
                    viewModel.addToDatabase(args.audioFile)
                }
                viewModel.setFav(!fav)
            }
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
        if (isBound) {
            requireActivity().unbindService(connection)
            isBound = false
        }
        viewModel.stop()
        binding = null
        super.onDestroyView()
    }
}