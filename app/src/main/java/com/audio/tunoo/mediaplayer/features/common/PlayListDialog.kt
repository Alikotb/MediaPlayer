package com.audio.tunoo.mediaplayer.features.common

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import com.audio.tunoo.mediaplayer.R

fun createPlaylistDialog(context: Context,onConfirmBtnClick : (String)-> Unit={},){
    val builder = AlertDialog.Builder(context)
    builder.setTitle(context.getString(R.string.new_playlist))
    val input = EditText(context)
    input.hint = context.getString(R.string.enter_playlist_name)
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
    builder.setPositiveButton(context.getString(android.R.string.ok)) { dialog, _ ->
        val playlistName = input.text.toString().trim()
        onConfirmBtnClick(playlistName)
        dialog.dismiss()
    }

    builder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
        dialog.dismiss()
    }

    builder.show()
}
