package net.dinkla.raytracer.utilities

import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

object AppProperties {

    var properties: Properties

    init {
        properties = Properties()
        try {
            properties.load(FileInputStream("raytracer.properties"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    operator fun get(key: Any): Any? = properties[key]

    fun getAsInteger(key: Any): Int = (properties[key] as String).toInt()

    fun getAsDouble(key: Any): Double = (properties[key] as String).toDouble()

}
