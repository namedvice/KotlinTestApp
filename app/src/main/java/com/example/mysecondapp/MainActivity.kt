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
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.IOException
import java.io.InputStream


//import dev.atsushieno.ktmidi.AndroidMidiAccess


public class MainActivity<string> : AppCompatActivity() {

    //    private var mMidiAccess: AndroidMidiAccess? = null
    var empDataHashMap = HashMap<String, String>()
    var empList: ArrayList<HashMap<String, String>> = ArrayList()

    private var mMidiManager: MidiManager? = null
    var bEditMode = false

    @RequiresApi(Build.VERSION_CODES.M)
    private val mDispatcher: MidiDispatcher = MidiDispatcher()
    private var mChannel // ranges from 0 to 15
            = 0
    private var mInputPort: MidiInputPort? = null
    private var mOutputPort: MidiOutputPort? = null

    private var mOpenDevice: MidiDevice? = null
    private var mDeviceOpened = false

    var selectedSquare: ControlElement? = null
    private var tempElements: List<ControlElement>? = null

    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

            val myInputStream: InputStream
            val myOutput: String
            val parser = XmlPullParserHandler(this)
            try {
                myInputStream = assets.open("bright_kalimba.xml")
                tempElements = parser.parse(this, myInputStream)
                val size: Int = myInputStream.available()
                val buffer = ByteArray(size)
                myInputStream.read(buffer)
                myOutput = String(buffer)

                // Sets the TextView with the string
                Log.e("Read Xml", "Done" + tempElements!!.elementAt(0).id.toString())

            } catch (e: IOException) {
                // Exception
                e.printStackTrace()
                Log.e("Read Xml", "Not DOne" + assets.open("bright_kalimba.xml").toString())
            }
        }

    }

    private fun setupEditButton() {
        val controlElementsTableRef = findViewById<TableLayout>(R.id.control_elements)
        val editModeButtonRef = findViewById<Button>(R.id.edit_mode_btn)
        val elementsMatrixRef = findViewById<TableLayout>(R.id.elements_matrix)
        checkElementsMenuUI(elementsMatrixRef, controlElementsTableRef)
        editModeButtonRef.setOnClickListener {
            bEditMode = !bEditMode
            editModeButtonRef.text = if (bEditMode) "Confirm" else "Edit"
            checkElementsMenuUI(elementsMatrixRef, controlElementsTableRef)
            Log.e("EditMode", bEditMode.toString())
        }
    }

    private fun checkElementsMenuUI(
        elementsMatrixRef: TableLayout, controlElementsTableRef: TableLayout
    ) {
        val elementOptionsRef = findViewById<TableLayout>(R.id.element_options)
        if (bEditMode) {
            controlElementsTableRef.visibility = View.VISIBLE
            elementsMatrixRef.visibility = View.VISIBLE
        } else {
            elementOptionsRef.visibility = View.INVISIBLE
            controlElementsTableRef.visibility = View.INVISIBLE
            elementsMatrixRef.visibility = View.INVISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupElementsTable() {
        val elementsTable = findViewById<TableRow>(R.id.control_elements_row1)
        //list of elements in table
        val elems = arrayOf("sliderCE", "buttonCE")
        for (elemTag in elems) {
            val newControlElement = TableElement(this, elemTag, 0, 0)
            newControlElement.setOnClickListener() {
                if (selectedSquare != null) {
                    val lPosition = IntArray(2)
                    selectedSquare!!.getLocationOnScreen(lPosition)
                    when (newControlElement.tag) {
                        "sliderCE" -> {
                            val newControlSlider =
                                ControlSlider(this, mInputPort, lPosition[0], lPosition[1])
                            findViewById<ConstraintLayout>(R.id.mainLayout).addView(newControlSlider)
                        }
                        "buttonCE" -> {
                            val newControlButton =
                                ControlButton(this, mInputPort, false, lPosition[0], lPosition[1])
                            findViewById<ConstraintLayout>(R.id.mainLayout).addView(newControlButton)
                        }
                    }
                }
            }
            elementsTable.addView(newControlElement)
        }
        val newControlElement = TableElement(this, "buttonCE", 0, 0)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupElementsMatrix() {
        val matrixView = findViewById<TableLayout>(R.id.elements_matrix)

        var matrixRow = TableRow(this)


        val rowsAmount = 4
        val columnsAmount = 8

        val emptyElementsMatrix = listOf<Int>()

        val array = Array(rowsAmount) { Array(columnsAmount) { mutableListOf<Int>(0, 0) } }
        //calculate matrix positions
        val matrixElements = mutableListOf<ControlElement>()
        val xStep = 200 //distance between Columns
        val yStep = 50 //distance between Rows
        for (i in array.indices) {
            for (j in array[i].indices) {
                array[i][j][0] = xStep * j
                array[i][j][1] = yStep * (i + 1)
                val matrixElement = ControlElement(this, array[i][j][0], array[i][j][1])
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
            debugMessage("Порт - это ${infos.elementAt(0)}")
            try {
                mMidiManager!!.openDevice(infos.elementAt(0),
                    MidiManager.OnDeviceOpenedListener { device ->
                        if (device == null) {
                            Log.e("OpenDevice", "could not open ${infos.elementAt(1)}")
                        } else {
                            mOpenDevice = device
                            mInputPort = device.openInputPort(
                                0
                            )
                            mOutputPort = device.openOutputPort(0)
                            if (mOutputPort == null) {
                                Log.e(
                                    "OpenDevice",
                                    "could not open output port for ${infos.elementAt(1)}"
                                )
                                return@OnDeviceOpenedListener
                            }
                            mOutputPort!!.connect(mDispatcher)
                            if (mInputPort == null) {
                                Log.e("OpenDevice", "could not open input port on ${infos.elementAt(1)}")
                                return@OnDeviceOpenedListener
                            }
                            mDeviceOpened = true
                        }
                    }, null)
                // Don't run the callback on the UI thread because this might take a while.
            } catch (e: Exception) {
                Log.e("OpenDevice", "openDevice failed", e)
            }
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
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        fun selectSquare(square: ControlElement) {
            selectedSquare?.deselect()
            selectedSquare = square
        }

    }
