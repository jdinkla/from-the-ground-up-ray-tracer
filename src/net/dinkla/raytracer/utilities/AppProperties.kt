package net.dinkla.raytracer.utilities

/*
 * Copyright (c) 2012 by Jörn Dinkla, www.dinkla.com, All rights reserved.
 */

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

    operator fun get(key: Any): Any? {
        return properties[key]
    }

    fun getAsInteger(key: Any): Int {
        return Integer.valueOf(properties[key] as String)
    }

}
