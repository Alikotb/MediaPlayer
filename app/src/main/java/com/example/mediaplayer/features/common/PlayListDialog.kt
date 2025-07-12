package com.example.mediaplayer.features.common

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast

fun createPlaylistDialog(context: Context,onConfirmBtnClick : (String)-> Unit={},){
    val builder = AlertDialog.Builder(context)
    builder.setTitle(" New Playlist")
    val input = EditText(context)
    input.hint = "Playlist name"
    input.inputType = InputType.TYPE_CLASS_TEXT

    val container = FrameLayout(context)
    val params = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.marginStart = 32
    params.marginEnd = 32
    input.layoutParams = params
    container.addView(input)

    builder.setView(container)
    builder.setPositiveButton("Confirm") { dialog, _ ->
        val playlistName = input.text.toString().trim()
        if (playlistName.isNotEmpty()) {
            Toast.makeText(context, "New Playlist: $playlistName", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show()
        }
        onConfirmBtnClick(playlistName)
        dialog.dismiss()
    }

    builder.setNegativeButton("Cancel") { dialog, _ ->
        dialog.dismiss()
    }

    builder.show()
}