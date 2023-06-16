package com.example.mysecondapp

import android.annotation.SuppressLint
import android.content.Context
import android.media.midi.MidiInputPort
import android.os.Build
import android.util.Log
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatSeekBar

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("ViewConstructor", "UseCompatLoadingForDrawables")
class ControlSlider(ctx: Context, mInputPort: MidiInputPort?, posX: Int, posY: Int) :
    AppCompatSeekBar(ctx) {

    init {
        minHeight = 1
        minWidth = 400
        max = 127
        x = posX.toFloat() - minWidth / 2
        y = posY.toFloat() - minWidth / 2
        Log.e("Position", posX.toString())
        Log.e("Position", posY.toString())
        rotation = 270f
        thumb = resources.getDrawable(
            R.drawable.custom_thumb
        )
        progressDrawable = resources.getDrawable(
            R.drawable.seekbar_progress
        )
        Log.e("SeekBar", progress.toString())

        var pitch = 61

        this.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.e("SeekBar", progress.toString())
                var buffer = ByteArray(32)
                var numBytes = 0
                val channel = 1 // MIDI channels 1-16 are encoded as 0-15.
                val group = 0
//                buffer[numBytes++] = (0x20 + group).toByte() // MIDI 1.0 voice message

                buffer[numBytes++] = (0x90 + (channel - 1)).toByte() // note on
                buffer[numBytes++] = pitch.toByte() // pitch is middle C
                buffer[numBytes++] = progress.toByte() // max velocity
                val offset = 0
                mInputPort?.send(buffer, offset, numBytes)

                //Sending note with velocity 0 to STOP it
                Log.e("Midi", mInputPort.toString())

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.e("SeekBar", progress.toString())

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.e("SeekBar", progress.toString())

            }

        })
    }


}