package com.example.mysecondapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import java.io.File

@SuppressLint("ViewConstructor")
class TableElement(ctx: Context, customTag: String, positionX: Int, positionY: Int) :
    ControlElement(ctx, positionX, positionY) {

    var imagePath: String? = null
    init {
        tag = customTag
        setElementVisuals()
    }

    private fun setElementVisuals() {
        //source of images should be there
        /*setImageBitmap(
            BitmapFactory.decodeFile("/sdcard/Images/$imagePath")
        )*/
    }
}