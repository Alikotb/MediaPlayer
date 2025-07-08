package com.example.mediaplayer.features.audio_player

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentAudioPlayerBinding
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.utils.getAlbumArt
import com.example.mediaplayer.utils.startUpdating
import com.example.mediaplayer.utils.stopUpdating
import com.example.mediaplayer.utils.toMinutesAndSeconds

class AudioPlayerFragment : Fragment() {
    private var currentAudio: AudioDto? = null
    var isPlaying = false
    var mediaPlayer: MediaPlayer? = null
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


        currentAudio  = AudioPlayerFragmentArgs.fromBundle(requireArguments()).audioFile
        val list = AudioPlayerFragmentArgs.fromBundle(requireArguments()).audioList
        mediaPlayer = MediaPlayer().apply { setDataSource(currentAudio?.path); prepare() }
        mediaPlayer?.start()
        isPlaying = true
        binding?.playPauseBtn?.setImageResource(R.drawable.baseline_pause_24)
        startUpdating(mediaPlayer!!, binding?.seekBar!!, binding?.startTimeTv!!)

        mediaPlayer?.setOnCompletionListener {
            playNextTrack()
        }
        binding?.root?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
        binding?.apply {
            trackNameAudioPlayerTv.apply {
                isSelected = true
                text = currentAudio?.title
            }
            trackArtistAudioPlayerTv.text = currentAudio?.artist ?: "غير معروف"
            totalTimeTv.text = mediaPlayer?.duration?.toLong()?.toMinutesAndSeconds()
        }

        binding?.playPauseBtn?.setOnClickListener {
            if (!isPlaying) {
                binding?.playPauseBtn?.setImageResource(R.drawable.baseline_pause_24)
                mediaPlayer?.start()
                startUpdating(mediaPlayer!!, binding?.seekBar!!, binding?.startTimeTv!!)
            } else {
                binding?.playPauseBtn?.setImageResource(R.drawable.baseline_play_arrow_24)
                mediaPlayer?.pause()
                stopUpdating()
            }
            isPlaying = !isPlaying

        }

        binding?.nextBtn?.setOnClickListener {
            val currentIndex = list.indexOf(currentAudio)
            currentAudio = if (currentIndex == list.size - 1) {
                list.first()
            } else {
                list[currentIndex + 1]
            }
            updateUIAndPlay(currentAudio!!)
        }

        binding?.previousBtn?.setOnClickListener {
            val currentIndex = list.indexOf(currentAudio)
            currentAudio = if (currentIndex == 0) {
                list.last()
            } else {
                list[currentIndex - 1]
            }
            updateUIAndPlay(currentAudio!!)
        }


        binding?.seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)

            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        mediaPlayer?.setOnCompletionListener {
            playNextTrack()
        }

        currentAudio?.path?.let { path ->
            Glide.with(requireContext())
                .asBitmap()
                .load(getAlbumArt(path))
                .placeholder(R.drawable.cylinder)
                .into(binding?.imageAudio ?: return@let)
        }

        binding?.backBtn?.setOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopUpdating()
        mediaPlayer?.release()
        binding = null
    }

    private fun updateUIAndPlay(audio: AudioDto) {
        mediaPlayer?.stop()
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(audio.path)
            prepare()
            start()
            setOnCompletionListener {
                playNextTrack()
            }
        }

        binding?.trackNameAudioPlayerTv?.text = audio.title
        binding?.trackArtistAudioPlayerTv?.text = audio.artist ?: "غير معروف"
        binding?.totalTimeTv?.text = mediaPlayer?.duration?.toLong()?.toMinutesAndSeconds()
        binding?.playPauseBtn?.setImageResource(R.drawable.baseline_pause_24)

        startUpdating(mediaPlayer!!, binding?.seekBar!!, binding?.startTimeTv!!)

        Glide.with(requireContext())
            .asBitmap()
            .load(getAlbumArt(audio.path))
            .placeholder(R.drawable.cylinder)
            .into(binding?.imageAudio ?: return)
    }

    private fun playNextTrack() {
        val list = AudioPlayerFragmentArgs.fromBundle(requireArguments()).audioList
        val currentIndex = list.indexOf(currentAudio)
        currentAudio = if (currentIndex == list.size - 1) {
            list.first()
        } else {
            list[currentIndex + 1]
        }
        currentAudio?.let { updateUIAndPlay(it) }
    }

}