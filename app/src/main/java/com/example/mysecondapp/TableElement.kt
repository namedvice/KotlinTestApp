package com.example.mysecondapp

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("ViewConstructor")
class TableElement(ctx: Context, customTag: String, positionX: Int, positionY: Int) :
    ControlElement(ctx, positionX, positionY) {
        init{
            tag = customTag
            setElementVisuals()
        }

    private fun setElementVisuals(){
        when(tag) {
            "sliderCE" -> setImageResource(R.drawable.slider)

        }
    }
}