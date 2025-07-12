package com.example.mediaplayer.features.audio_player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentAudioPlayerBinding
import com.example.mediaplayer.features.common.createPlaylistDialog
import com.example.mediaplayer.service.MyMediaService
import com.example.mediaplayer.utils.getAlbumArt
import com.example.mediaplayer.utils.getCustomGradientList
import com.example.mediaplayer.utils.toMinutesAndSeconds
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import androidx.core.net.toUri

class AudioPlayerFragment : Fragment() {
    private val viewModel: AudioPlayerViewModel by inject()
    private var binding: FragmentAudioPlayerBinding? = null
    private var service: MyMediaService? = null
    private var isBound = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val mediaBinder = binder as MyMediaService.MediaBinder
            service = mediaBinder.getService()
            val args = AudioPlayerFragmentArgs.fromBundle(requireArguments())
            service?.binder?.setList(args.audioList.toList(), args.audioFile!!)
            service?.play(args.audioFile!!)
            isBound = true
            setupServiceCollectors()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.root?.background = getCustomGradientList().random()
        val args = AudioPlayerFragmentArgs.fromBundle(requireArguments())

        viewModel.checkFava(args.audioFile!!)

        val serviceIntent = Intent(requireContext(), MyMediaService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(serviceIntent)
        }
        requireActivity().bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)

        setupUiListeners(args,requireContext())
        observeFavoriteState()
    }

    private fun setupServiceCollectors() {
        service?.let { safeService ->
            lifecycleScope.launchWhenStarted {
                safeService.isPlaying.collect { playing ->
                    val icon =
                        if (playing) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                    binding?.playPauseBtn?.setImageResource(icon)
                }
            }


            lifecycleScope.launch {
                safeService.currentDuration.collectLatest { progress ->
                    binding?.seekBar?.progress = progress.toInt()
                    binding?.startTimeTv?.text = progress.toLong().toMinutesAndSeconds()
                }
            }

            lifecycleScope.launch {
                safeService.maxDuration.collectLatest { duration ->
                    binding?.seekBar?.max = duration.toInt()
                    binding?.totalTimeTv?.text = duration.toLong().toMinutesAndSeconds()
                }
            }

            lifecycleScope.launch {
                safeService.currentTrack.collectLatest { track ->
                    track?.let {
                        binding?.trackNameAudioPlayerTv?.apply {
                            text = it.title
                            isSelected = true
                        }
                        binding?.trackArtistAudioPlayerTv?.text = it.artist ?: "غير معروف"
                        val rotate = RotateAnimation(
                            0f, 360f,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f
                        ).apply {
                            duration = 4000
                            repeatCount = Animation.INFINITE
                            interpolator = LinearInterpolator()
                        }

                        binding?.imageAudio?.setImageResource(R.drawable.cylinder)
                        binding?.imageAudio?.startAnimation(rotate)
                        Glide.with(requireContext())
                            .asBitmap()
                            .load(getAlbumArt(it.path))
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    binding?.imageAudio?.setImageBitmap(resource)
                                    binding?.imageAudio?.clearAnimation()
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                    binding?.imageAudio?.setImageDrawable(placeholder)
                                    binding?.imageAudio?.clearAnimation()
                                }
                            })


                        viewModel.addToHistory(it)
                        viewModel.checkFava(it)
                    }
                }
            }
        }
    }

    private fun setupUiListeners(args: AudioPlayerFragmentArgs,context: Context) {
        binding?.menuBtn?.setOnClickListener {
            val dialog = BottomSheetDialog(context)
            val audio = args.audioFile
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            val cancelTv : TextView = view.findViewById(R.id.cancel_tv)
            val setAsRingtoneTv : LinearLayout = view.findViewById(R.id.set_as_ringtone_row)
            val shareFileTv : LinearLayout = view.findViewById(R.id.share_file_row)
            val addToPlaylistTv : LinearLayout = view.findViewById(R.id.add_to_playlist_row)
            cancelTv.setOnClickListener {
                dialog.dismiss()
            }
            addToPlaylistTv.setOnClickListener {
                dialog.dismiss()
                val subDialog = BottomSheetDialog(context)

                val subView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_playlist, null)
                val cancelTv : TextView = subView.findViewById(R.id.cancel_tv)
                val newList : LinearLayout = subView.findViewById(R.id.new_playlist_row)
                val favRow : LinearLayout = subView.findViewById(R.id.add_to_Fav_row)
                val playlistRecyclerView : RecyclerView = subView.findViewById(R.id.playlist_sheet_recyclerView)
                cancelTv.setOnClickListener {
                    subDialog.dismiss()
                }
                favRow.setOnClickListener {
                    viewModel.addToDatabase(audio!!)
                    viewModel.setFav(true)
                    Toast.makeText(context, "Added To favorite successfully", Toast.LENGTH_SHORT).show()

                }
                newList.setOnClickListener {
                    createPlaylistDialog(requireContext()){
                    }
                }

                subDialog.setCancelable(true)
                subDialog.setCanceledOnTouchOutside(true)
                subDialog.setContentView(subView)
                subDialog.show()
            }
            shareFileTv.setOnClickListener {
                val context = requireContext()
                val audioFile = audio ?: return@setOnClickListener
                val file = File(audioFile.path)

                if (!file.exists()) {
                    Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "audio/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                context.startActivity(Intent.createChooser(shareIntent, "Share Audio"))
                dialog.dismiss()
            }


            setAsRingtoneTv.setOnClickListener {
                val context = requireContext()
                val audioFile =audio ?: return@setOnClickListener
                val file = File(audioFile.path)

                if (!file.exists()) {
                    Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(context)) {
                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                        data = "package:${context.packageName}".toUri()
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                    return@setOnClickListener
                }

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                context.grantUriPermission(
                    "com.android.providers.media.module",
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                Settings.System.putString(
                    context.contentResolver,
                    Settings.System.RINGTONE,
                    uri.toString()
                )

                Toast.makeText(context, "Ringtone Set!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(view)
            dialog.show()
        }


        binding?.playPauseBtn?.setOnClickListener {
            service?.playPause()
        }

        binding?.favBtn?.setOnClickListener {
            viewModel.isFav.value.let { fav ->
                if (fav) {
                    viewModel.deleteFromDatabase(args.audioFile!!)
                } else {
                    viewModel.addToDatabase(args.audioFile!!)
                }
                viewModel.setFav(!fav)
            }
        }

        binding?.nextBtn?.setOnClickListener {
            service?.next()
            binding?.root?.background = getCustomGradientList().random()

        }

        binding?.previousBtn?.setOnClickListener {
            service?.prev()
            binding?.root?.background = getCustomGradientList().random()

        }

        binding?.seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) service?.seekTo(progress)
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        binding?.backBtn?.setOnClickListener {
            val exitAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_to_bottom)
            binding?.root?.startAnimation(exitAnim)

            binding?.root?.postDelayed({
                findNavController().popBackStack()
            }, exitAnim.duration)
        }


    }

    private fun observeFavoriteState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFav.collect { fav ->
                val icon =
                    if (fav) R.drawable.baseline_favorite_24 else R.drawable.outline_favorite_24
                binding?.favBtn?.setImageResource(icon)
            }
        }
    }

    override fun onDestroyView() {
        if (isBound) {
            requireActivity().unbindService(connection)
            isBound = false
        }
        binding = null
        super.onDestroyView()
    }
}