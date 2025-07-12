package com.example.mediaplayer.features.common

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mediaplayer.R
import com.google.android.material.snackbar.Snackbar
import android.view.LayoutInflater


@SuppressLint("InflateParams")
fun showCustomSnackbar(view: View, message: String) {

    val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
    val snackbarLayout = snackbar.view as ViewGroup

    snackbarLayout.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).visibility = View.INVISIBLE

    snackbarLayout.setBackgroundColor(Color.TRANSPARENT)

    val customSnackView = LayoutInflater.from(view.context).inflate(R.layout.snackbar_custom, null)
    customSnackView.findViewById<TextView>(R.id.snackbar_text).text = message

    snackbarLayout.addView(customSnackView, 0)

    snackbar.show()

}

