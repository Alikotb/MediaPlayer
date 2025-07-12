
package com.example.mediaplayer.features.audio_player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentAudioPlayerBinding
import com.example.mediaplayer.features.Response
import com.example.mediaplayer.features.audio_player.view.PlayListAdapter
import com.example.mediaplayer.features.common.createPlaylistDialog
import com.example.mediaplayer.features.common.showCustomSnackbar
import com.example.mediaplayer.features.playlist.PlayListViewModel
import com.example.mediaplayer.model.dto.AudioDto
import com.example.mediaplayer.model.dto.PlaylistDto
import com.example.mediaplayer.service.MyMediaService
import com.example.mediaplayer.utils.getAlbumArt
import com.example.mediaplayer.utils.getCustomGradientList
import com.example.mediaplayer.utils.toMinutesAndSeconds
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File

class AudioPlayerFragment : Fragment() {
    private val viewModel: AudioPlayerViewModel by inject()
    private val playlistViewModel: PlayListViewModel by inject()
    private var binding: FragmentAudioPlayerBinding? = null
    private var service: MyMediaService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val args = AudioPlayerFragmentArgs.fromBundle(requireArguments())
            val mediaBinder = binder as MyMediaService.MediaBinder
            service = mediaBinder.getService()
            service?.apply {
                // تأكد إن التراك الحالي مختلف عن التراك الجديد
                val current = currentTrack.value
                if (current?.path != args.audioFile?.path) {
                    this.binder.setList(args.audioList.toList(), args.audioFile!!)
                    play(args.audioFile)
                }
            }

            isBound = true
            setupServiceCollectors()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            isBound = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.root?.background = getCustomGradientList().random()
        val args = AudioPlayerFragmentArgs.fromBundle(requireArguments())
        viewModel.checkFava(args.audioFile!!)



        val serviceIntent = Intent(requireContext(), MyMediaService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) requireActivity().startForegroundService(serviceIntent)
        requireActivity().bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)

        setupUiListeners()
        observeFavoriteState()
    }

    private fun setupUiListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.snackbarFlow.collect { message ->
                binding?.root?.let {
                    showCustomSnackbar(it,message)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            playlistViewModel.snackbarFlow.collect { message ->
                binding?.root?.let {
                    showCustomSnackbar(it,message)

                }
            }
        }

        binding?.apply {
            playPauseBtn.setOnClickListener { service?.playPause() }
            nextBtn.setOnClickListener {
                service?.next()
                root.background = getCustomGradientList().random()
            }
            previousBtn.setOnClickListener {
                service?.prev()
                root.background = getCustomGradientList().random()
            }
            backBtn.setOnClickListener {
                val exitAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_to_bottom)
                root.startAnimation(exitAnim)
                root.postDelayed({ findNavController().popBackStack() }, exitAnim.duration)
            }
            favBtn.setOnClickListener {
                val currentTrack = service?.currentTrack?.value ?: return@setOnClickListener
                viewModel.isFav.value.let { fav ->
                    if (fav) viewModel.deleteFromDatabase(currentTrack) else viewModel.addToDatabase(currentTrack)
                    viewModel.setFav(!fav)
                }
            }
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) service?.seekTo(progress)
                }
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            })
            menuBtn.setOnClickListener {
                showMainMenuDialog(requireContext(), service?.currentTrack?.value ?: return@setOnClickListener)
            }
        }
    }

    private fun setupServiceCollectors() {
        service?.let { svc ->
            lifecycleScope.launch {
                svc.isPlaying.collect { playing ->
                    val icon = if (playing) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                    binding?.playPauseBtn?.setImageResource(icon)
                }
            }
            lifecycleScope.launch {
                svc.currentDuration.collectLatest {
                    binding?.seekBar?.progress = it.toInt()
                    binding?.startTimeTv?.text = it.toLong().toMinutesAndSeconds()
                }
            }
            lifecycleScope.launch {
                svc.maxDuration.collectLatest {
                    binding?.seekBar?.max = it.toInt()
                    binding?.totalTimeTv?.text = it.toLong().toMinutesAndSeconds()
                }
            }
            lifecycleScope.launch {
                svc.currentTrack.collectLatest { track ->
                    track?.let { updateUiForTrack(it) }
                }
            }
        }
    }

    private fun updateUiForTrack(track: AudioDto) {
        binding?.trackNameAudioPlayerTv?.apply {
            text = track.title
            isSelected = true
        }
        binding?.trackArtistAudioPlayerTv?.text = track.artist ?: "غير معروف"
        val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 4000
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }
        binding?.imageAudio?.apply {
            setImageResource(R.drawable.cylinder)
            startAnimation(rotate)
        }
        Glide.with(requireContext())
            .asBitmap()
            .load(getAlbumArt(track.path))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding?.imageAudio?.setImageBitmap(resource)
                    binding?.imageAudio?.clearAnimation()
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    binding?.imageAudio?.setImageDrawable(placeholder)
                    binding?.imageAudio?.clearAnimation()
                }
            })
        viewModel.addToHistory(track)
        viewModel.checkFava(track)
    }

    @SuppressLint("InflateParams")
    private fun showMainMenuDialog(context: Context, audio: AudioDto) {
        val dialog = BottomSheetDialog(context)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

        view.findViewById<TextView>(R.id.cancel_tv).setOnClickListener { dialog.dismiss() }
        view.findViewById<LinearLayout>(R.id.set_as_ringtone_row).setOnClickListener {
            setAsRingtone(audio)
            dialog.dismiss()
        }
        view.findViewById<LinearLayout>(R.id.share_file_row).setOnClickListener {
            shareAudio(audio)
            dialog.dismiss()
        }
        view.findViewById<LinearLayout>(R.id.add_to_playlist_row).setOnClickListener {
            dialog.dismiss()
            showAddToPlaylistDialog(context, audio)
        }

        dialog.setContentView(view)
        dialog.show()
    }

    @SuppressLint("InflateParams")
    private fun showAddToPlaylistDialog(context: Context, audio: AudioDto) {
        val dialog = BottomSheetDialog(context)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_playlist, null)
        val adapter = PlayListAdapter(mutableListOf()) { playlistName ->
            playlistViewModel.insertAudioInPlayList(audio, playlistName)
            dialog.dismiss()

        }
        view.findViewById<RecyclerView>(R.id.playlist_sheet_recyclerView).adapter = adapter

        playlistViewModel.getAllPlayList()
        lifecycleScope.launch {
            playlistViewModel.namesState.collectLatest {
                if (it is Response.Success) adapter.updateList(it.data)
            }
        }

        view.findViewById<TextView>(R.id.cancel_tv).setOnClickListener { dialog.dismiss() }
        view.findViewById<LinearLayout>(R.id.add_to_Fav_row).setOnClickListener {
            viewModel.addToDatabase(audio)
            viewModel.setFav(true)
            dialog.dismiss()

        }
        view.findViewById<LinearLayout>(R.id.new_playlist_row).setOnClickListener {
            createPlaylistDialog(context) { name ->
                if (name.isNotBlank()) playlistViewModel.addNewList(PlaylistDto(name, listOf(audio)))
            }
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun shareAudio(audio: AudioDto) {
        val file = File(audio.path)
        if (!file.exists()) {
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_SHORT).show()
            return
        }
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share Audio"))
    }
    private fun setAsRingtone(audio: AudioDto) {
        val file = File(audio.path)
        if (!file.exists()) {
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Settings.System.canWrite(requireContext())) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                data = "package:${requireContext().packageName}".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            return
        }

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/*")
            put(MediaStore.Audio.Media.IS_RINGTONE, true)
            put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            put(MediaStore.Audio.Media.IS_ALARM, false)
            put(MediaStore.Audio.Media.IS_MUSIC, false)
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Ringtones/")
        }

        val resolver = requireContext().contentResolver
        val uri = MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)
        val newUri = resolver.insert(uri!!, values)

        if (newUri != null) {
            resolver.openOutputStream(newUri).use { outputStream ->
                file.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream!!)
                }
            }

            Settings.System.putString(
                resolver,
                Settings.System.RINGTONE,
                newUri.toString()
            )
            Toast.makeText(requireContext(), "Ringtone Set!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Failed to set ringtone", Toast.LENGTH_SHORT).show()
        }
    }


    private fun observeFavoriteState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFav.collect {
                val icon = if (it) R.drawable.baseline_favorite_24 else R.drawable.outline_favorite_24
                binding?.favBtn?.setImageResource(icon)
            }
        }
    }

    override fun onDestroyView() {
        if (isBound) requireActivity().unbindService(connection)
        binding = null
        super.onDestroyView()
    }
}
