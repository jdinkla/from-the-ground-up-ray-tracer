package net.dinkla.raytracer.gui

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

private val df = SimpleDateFormat("yyyyMMddHHmmss")

fun getOutputPngFileName(fileName: String): String {
    var outFileName = fileName.replace(".[a-zA-Z0-9]+$".toRegex(), "")
    outFileName = "../" + df.format(Date()) + "_" + outFileName + ".png"
    return outFileName
}

fun extractFileName(file: File, directory: File)= extractFileName(file.absoluteFile.toString(), directory.absolutePath.toString())

fun extractFileName(file: String, directory: String, separator: String = File.separator): String {
    return file.replaceFirst(directory + separator, "")
}
