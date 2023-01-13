package com.example.mysecondapp

import android.R
import android.view.View
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ScaleDrawable
import android.media.midi.MidiInputPort
import android.os.Build
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TableLayout
import androidx.annotation.RequiresApi
import androidx.core.widget.TextViewCompat.setCompoundDrawablesRelative
import androidx.core.widget.addTextChangedListener


@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("ViewConstructor", "UseCompatLoadingForDrawables")
class ControlButton(
    ctx: Context, mInputPort: MidiInputPort?, toggleMode: Boolean, /*ID: Int, */
    positionX: Int, positionY: Int
) : ControlElement(ctx, positionX, positionY) {

    private val isToggle = toggleMode


    //    val buttonId = ID
    init {
        x = positionX.toFloat()
        y = positionY.toFloat()
        maxWidth = 50
        maxWidth = 50
        setBackgroundResource(R.drawable.checkbox_on_background)
        var pitch = 60
        val pitchSelector = EditText(ctx as MainActivity<*>)
        pitchSelector.setText("90")
/*
    layoutParams = LinearLayout.LayoutParams(100, 100);
*/
        setOnClickListener {
            if ((ctx as MainActivity<*>).bEditMode) {
                val temp: TableLayout =
                    (ctx as MainActivity<*>).findViewById<TableLayout>(com.example.mysecondapp.R.id.element_options)
                temp.visibility = VISIBLE
                temp.removeAllViews()
                pitchSelector.addTextChangedListener {
                    pitch = try {
                        pitchSelector.text.toString().toInt()

                    } catch (ex: NumberFormatException) {
                        0
                    }
                    Log.e("MidiPitch", pitchSelector.text.toString())
                }
                (ctx as MainActivity<*>).findViewById<TableLayout>(com.example.mysecondapp.R.id.element_options)
                    .addView(pitchSelector)

            } else {
                //sending note with velocity 127 to START it
                var buffer = ByteArray(32)
                var numBytes = 0
                val channel = 1 // MIDI channels 1-16 are encoded as 0-15.
                buffer[numBytes++] = (0x90 + (channel - 1)).toByte() // note on
                buffer[numBytes++] = pitch.toByte() // pitch is middle C
                buffer[numBytes++] = 127.toByte() // max velocity
                val offset = 0
                mInputPort?.send(buffer, offset, numBytes)

                //Sending note with velocity 0 to STOP it
                buffer = ByteArray(32)
                numBytes = 0
                buffer[numBytes++] = (0x90 + (channel - 1)).toByte() // note on
                buffer[numBytes++] = pitch.toByte() // pitch is middle C
                buffer[numBytes++] = 0.toByte() // max velocity
                mInputPort?.send(buffer, offset, numBytes)
                Log.e("Midi", mInputPort.toString())
            }
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isToggle) {

        }

    }


}