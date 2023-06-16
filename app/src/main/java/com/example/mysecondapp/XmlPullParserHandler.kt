package com.example.mysecondapp

import android.content.Context
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream

class XmlPullParserHandler (ctx: Context) {
    private val controlElements = ArrayList<TableElement>()
    private var controlElement: TableElement? = null
    private var text: String? = null

    fun parse(ctx: Context, inputStream: InputStream): List<TableElement> {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> if (tagname.equals("tableElement", ignoreCase = true)) {
                        // create a new instance of controlElement
                        controlElement = TableElement(ctx, " ", 0, 0,)
                    }

                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> if (tagname.equals("tableElement", ignoreCase = true)) {
                        // add controlElement object to list
                        controlElement?.let { controlElements.add(it) }

                    }
                    // check all the tags that you need here!
                    else if (tagname.equals("imagePath", ignoreCase = true)) {
                        controlElement!!.imagePath = text
                    }

                    /* else if (tagname.equals("name", ignoreCase = true)) {
                        controlElement!!.name = text
                    } else if (tagname.equals("salary", ignoreCase = true)) {
                        controlElement!!.salary = java.lang.Float.parseFloat(text)
                    }*/

                    else -> {
                    }
                }
                eventType = parser.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return controlElements
    }
}