package com.example.mediaplayer.utils

import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.toColorInt
import kotlin.random.Random


fun getCustomGradientList(): List<GradientDrawable> {
    return listOf(
        createGradientDrawable("#FFeba34e", "#FFecb16b", "#FFdfaa68", "#FFa7763c","#FF8b683e","#FF85623a"),
        createGradientDrawable("#FFee891f", "#FFeb973d", "#FFd38636", "#FFa96117","#FFb1722f","#FF88551e"),
        createGradientDrawable("#FF707068", "#FF6f6f67", "#FF8a8c89", "#FF5d5852","#FF3f403b","#FF797b78"),
        createGradientDrawable("#FF984235", "#FF56251e", "#FFa95b4f", "#FF974134","#FF7d3527","#FF9c5448"),
        createGradientDrawable("#FF892d3a", "#FF882e3a", "#FF501b23", "#FF6d2938","#FF7b2d3a","#FF4d1a21"),
        createGradientDrawable("#FFb21a19", "#FF630f0f", "#FF962c2c", "#FF861f18","#FFb82827","#FFaf1b1b"),

        )
}

private fun createGradientDrawable(
    hex1: String,
    hex2: String,
    hex3: String,
    hex4: String,
    hex5: String,
    hex6: String,
): GradientDrawable {
    val colors = intArrayOf(
        hex1.toColorInt(),
        hex2.toColorInt(),
        hex3.toColorInt(),
        hex4.toColorInt(),
        hex5.toColorInt(),
        hex6.toColorInt(),
    )

    val orientations = GradientDrawable.Orientation.entries.toTypedArray()
    val randomOrientation = orientations[Random.nextInt(orientations.size)]

    return GradientDrawable(randomOrientation, colors).apply {
        this.cornerRadius = cornerRadius
    }
}

