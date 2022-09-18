package net.dinkla.raytracer.gui

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime

private val dateFormat = DateFormat("yyyyMMddHHmmss")

fun outputPngFileName(fileName: String, timeStamp: DateTime = DateTime.now()): String {
    val fileNameWithoutExtension = fileName.replace(".[a-zA-Z0-9]+$".toRegex(), "")
    return timeStamp.format(dateFormat) + "_" + fileNameWithoutExtension + ".png"
}

fun fileNameWithoutDirectory(fileName: String, directory: String, separator: String): String {
    return fileName.replaceFirst(directory + separator, "")
}
