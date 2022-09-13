package net.dinkla.raytracer.interfaces

import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Properties

object AppProperties {

    private var properties: Properties = Properties()

    init {
        try {
            val fileInputStream = FileInputStream("raytracer.properties")
            val reader = InputStreamReader(fileInputStream, "UTF-8")
            properties.load(reader)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    operator fun get(key: Any): Any? = properties[key]

    fun getAsInteger(key: Any): Int = (properties[key] as String).toInt()

    fun getAsDouble(key: Any): Double = (properties[key] as String).toDouble()

}
