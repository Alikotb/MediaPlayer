package com.example.mediaplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.mediaplayer.features.home.SplashFragmentDirections
import com.example.mediaplayer.model.dto.AudioDto

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE_AUDIO_PERMISSION = 101
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_background)
        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = !isDarkMode

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        window.decorView.post {
            handleIntent(intent)
        }
        checkAndRequestAudioPermission()
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun checkAndRequestAudioPermission() {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, audioPermission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(audioPermission)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_CODE_AUDIO_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_AUDIO_PERMISSION) {
            for (i in permissions.indices) {
                when (permissions[i]) {
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Audio access permission denied", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Manifest.permission.POST_NOTIFICATIONS -> {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        handleIntent(intent)
//    }

    private fun handleIntent(intent: Intent?) {
        val audio = intent?.getParcelableExtra<AudioDto>("audio")
        val audioList = intent?.getParcelableArrayListExtra<AudioDto>("audioList")

        if (audio != null && audioList != null) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_graph) as NavHostFragment
            val navController = navHostFragment.navController

            val action = SplashFragmentDirections.actionSplashFragmentToAudioPlayerFragment(
                audioFile = audio,
                audioList = audioList.toTypedArray()
            )

            if (navController.currentDestination?.id != R.id.audioPlayerFragment) {
                navController.navigate(action)
            }
        }
    }


}
