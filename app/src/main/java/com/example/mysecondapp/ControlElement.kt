package com.example.mysecondapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton


@SuppressLint("ViewConstructor", "UseCompatLoadingForDrawables")
open class ControlElement(ctx: Context, positionX: Int, positionY: Int) :
    AppCompatImageButton(ctx) {

    private var elementSelected = false
    private var defaultHeight = 150
    private var defaultWidth = 150


    init {
        tag = "btXYZ";
        minimumHeight = defaultHeight
        minimumWidth = defaultHeight
        maxHeight = defaultHeight + 100
        maxWidth = defaultWidth + 100
        Log.e("ControlElement", "Height is $height")
        Log.e("ControlElement", "Width is $width")
        this.setOnClickListener {
            (ctx as MainActivity<*>).findViewById<TableLayout>(R.id.element_options).visibility = INVISIBLE
            (ctx as MainActivity<*>).findViewById<TableLayout>(R.id.control_elements).visibility = VISIBLE
            (ctx as MainActivity<*>).selectedSquare?.deselect()
            Log.e("Control Element", elementSelected.toString())
            maxHeight = defaultHeight + 10
            maxWidth = defaultWidth + 10
            Log.e("ControlElement", "Context is $ctx")
            ctx.selectedSquare = this
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        Log.e("ControlElement", "Height is $height")
        Log.e("ControlElement", "Width is $width")
    }

    private fun updateUI() {
        if (elementSelected) {
            this.background = context.getDrawable(R.drawable.ic_launcher_background)
        } else {
            this.background = context.getDrawable(R.drawable.ic_launcher_foreground)
        }
        Log.e("ControlElement", "Height is $height")
        Log.e("ControlElement", "Width is $width")
    }

    fun deselect() {
        maxHeight = defaultHeight
        maxWidth = defaultWidth
    }
}