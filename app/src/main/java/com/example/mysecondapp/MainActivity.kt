package com.example.mysecondapp


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.midi.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.google.android.material.slider.Slider
import dev.atsushieno.ktmidi.AndroidMidiAccess


public class MainActivity<string> : AppCompatActivity() {

    private var mMidiAccess: AndroidMidiAccess? = null

    private var mMidiManager: MidiManager? = null

    @RequiresApi(Build.VERSION_CODES.M)
    private val mDispatcher: MidiDispatcher = MidiDispatcher()
    private var mChannel // ranges from 0 to 15
            = 0
    private var mInputPort: MidiInputPort? = null

    var selectedSquare: ControlElement? = null


    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val ll = findViewById<View>(R.id.activity_main) as ConstraintLayout
//        ll.addView(myButton)

//        val inflater = ContextCompat.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        parent!!.addView(ControlElement)
//        for (i in 0..3) {
//
//        }
        setupElementsMatrix()
        setupElementsTable()
        setupEditButton()

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            setupMidi()

//            var button = findViewById<Button>(R.id.btn_SendNote)
//            var temp = 60
//            button.setOnClickListener {
//                var buffer = ByteArray(32)
//                var numBytes = 0
//                var channel = 1 // MIDI channels 1-16 are encoded as 0-15.
//                buffer[numBytes++] = (0x90 + (channel - 1)).toByte() // note on
//                buffer[numBytes++] = temp.toByte() // pitch is middle C
//                buffer[numBytes++] = 127.toByte() // max velocity
//                var offset = 0
//                temp++
//                mInputPort?.send(buffer, offset, numBytes)
//                Log.e("Midi", mInputPort.toString())
//            }

            // post is non-blocking


        }
    }

    private fun setupEditButton() {
        val controlElementsTable = findViewById<TableLayout>(R.id.control_elements)
        val editModeButton = findViewById<Button>(R.id.edit_mode_btn)
        editModeButton.setOnClickListener {
            if (controlElementsTable.isVisible) {
                controlElementsTable.visibility = View.INVISIBLE
            } else {
                controlElementsTable.visibility = View.VISIBLE
            }
            Log.e("Layout", controlElementsTable.isVisible.toString())

        }
    }

    private fun setupElementsTable() {
        val elementsTable = findViewById<TableLayout>(R.id.control_elements)
        for (i in 1..3) {
            val newControlElement = Button(this)
            newControlElement.tag = "sliderCE"
            newControlElement.setOnClickListener() {
                if (selectedSquare != null && newControlElement.tag == "sliderCE") {
                    var newControlSlider = ControlSlider(this, selectedSquare!!.x.toInt(), selectedSquare!!.y.toInt())
                    findViewById<ConstraintLayout>(R.id.mainLayout).addView(newControlElement)
                }
            }
            elementsTable.addView(newControlElement)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupElementsMatrix() {
        val matrixView = findViewById<TableLayout>(R.id.elements_matrix)

        var matrixRow = TableRow(this)

        val XStep = 200 //distance between Columns
        val YStep = 50 //distance between Rows

        val rowsAmount = 4
        val columnsAmount = 8

        val emptyElementsMatrix = listOf<Int>()

        val array = Array(rowsAmount) { Array(columnsAmount) { mutableListOf<Int>(0, 0) } }
        //calculate matrix positions
        val matrixElements = mutableListOf<ControlElement>()
        for (i in array.indices) {
            for (j in array[i].indices) {
                array[i][j][0] = XStep * j
                array[i][j][1] = YStep * (i + 1)

                val matrixElement =
                    ControlElement(this, array[i][j][0], array[i][j][1])
                matrixElements.add(matrixElement)
                matrixRow.addView(matrixElement)
//                Log.e("Matrix", matrixElements[j].left.toString())
                if (j == 5) {  //check for j equal columns amount
                    matrixView.addView(matrixRow)
                    matrixRow = TableRow(this)

                }

            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setupMidi() {
        debugMessage("Setuping midi...")
        mMidiManager = getSystemService(MIDI_SERVICE) as MidiManager
        if (mMidiManager == null) {
            debugMessage("No MidiService")
            return
        }

        val infos: Array<MidiDeviceInfo> = mMidiManager!!.devices
        debugMessage(
            infos.elementAt(0).properties.getString(MidiDeviceInfo.PROPERTY_NAME).toString()
        )

        if (mMidiManager !== null) {
            mMidiManager?.openDevice(
                infos.elementAt(1), { device ->
                    if (device === null) {
                        debugMessage("No port found!!!")
                    } else {

                        mInputPort = device.openInputPort(0)
                        Log.e("Midi", "Port opened on ${infos.elementAt(1).properties}")
                        Log.e("Midi", "Input port: " + mInputPort.toString())
                        if (mInputPort == null) {
                            Log.e("Midi", "could not open input port on ${infos.elementAt(0)}")
                        }
                    }

                }, Handler(Looper.getMainLooper())
            )
        }
//        mMidiManager!!.openDevice(infos.elementAt(0), OnDeviceOpenedListener { device ->
//            if (device == null) {
//                Log.e("Midi", "could not open openPort")
//            } else {
////                    mOpenDevice = device
//                mOutputPort = device.openOutputPort(0)
//                mOutputPort!!.connect(mDispatcher)
//            }
//        }, null)
    }

    private fun debugMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG)
            .show()
    }

    fun selectSquare(square: ControlElement) {
        selectedSquare?.deselect()
        selectedSquare = square
    }

}
