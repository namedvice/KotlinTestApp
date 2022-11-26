package com.example.mysecondapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas

@SuppressLint("ViewConstructor")
class ControlButton(ctx: Context, toggleMode: Boolean, ID: Int, positionX: Int, positionY: Int) :
    ControlElement(ctx, positionX, positionY) {

    private val isToggle = toggleMode
    val buttonId = ID


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isToggle) {

        }
    }


}