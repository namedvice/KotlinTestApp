package com.example.mysecondapp


import android.app.*
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager


import android.media.midi.MidiReceiver

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.mysecondapp.databinding.ActivityMainBinding
import java.util.*

import com.mobileer.miditools.MidiConstants

import com.mobileer.miditools.MidiInputPortSelector

import com.mobileer.miditools.MusicKeyboardView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var mKeyboardReceiverSelector: MidiInputPortSelector? = null
    private var mKeyboard: MusicKeyboardView? = null
    private val mProgramButton: Button? = null
    private var mMidiManager: MidiManager? = null
    private val mChannel // ranges from 0 to 15
            = 0
    private val mPrograms = IntArray(MidiConstants.MAX_CHANNELS) // ranges from 0 to 127

    private val mByteBuffer = ByteArray(3)

    @RequiresApi(Build.VERSION_CODES.M)
    var infos: Array<MidiDeviceInfo> = m.getDevices()

    @RequiresApi(Build.VERSION_CODES.M)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txt: TextView = findViewById(R.id.tv_debug)

        print(txt)
    }

    private fun setupMidi() {
        mMidiManager = getSystemService(MIDI_SERVICE) as MidiManager
        if (mMidiManager == null) {
            Toast.makeText(this, "MidiManager is null!", Toast.LENGTH_LONG)
                .show()
            return
        }

        // Setup Spinner that selects a MIDI input port.
        mKeyboardReceiverSelector = MidiInputPortSelector(
            mMidiManager,
            this, R.id.spinner_receivers
        )
        mKeyboard = findViewById<View>(R.id.musicKeyboardView) as MusicKeyboardView
        mKeyboard.addMusicKeyListener(object : MusicKeyListener() {
            fun onKeyDown(keyIndex: Int) {
                noteOn(mChannel, keyIndex, MainActivity.DEFAULT_VELOCITY)
            }

            fun onKeyUp(keyIndex: Int) {
                noteOff(mChannel, keyIndex, MainActivity.DEFAULT_VELOCITY)
            }
        })
    }

}