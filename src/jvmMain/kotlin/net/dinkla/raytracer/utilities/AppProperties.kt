package net.dinkla.raytracer.utilities

import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Properties

object AppProperties {

    private var properties: Properties = Properties()

    init {
        try {
            properties.load(InputStreamReader(FileInputStream("raytracer.properties"), "ISO-8859-1"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    operator fun get(key: Any): Any? = properties[key]

    fun getAsInteger(key: Any): Int = (properties[key] as String).toInt()

    fun getAsDouble(key: Any): Double = (properties[key] as String).toDouble()

}
