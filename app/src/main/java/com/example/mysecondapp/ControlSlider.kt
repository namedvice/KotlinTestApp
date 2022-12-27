package com.example.mysecondapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

@SuppressLint("ViewConstructor", "UseCompatLoadingForDrawables")
class ControlSlider(ctx: Context, positionX: Int, positionY: Int) :
    AppCompatSeekBar(ctx) {

    init {
        x=positionX.toFloat()
        y=positionY.toFloat()
        minimumHeight = 100
        minimumWidth = 200
        rotation = -90f
        thumb = resources.getDrawable(
            R.drawable.custom_thumb
        )
        progressDrawable = resources.getDrawable(
            R.drawable.seekbar_progress
        )
    }


}