package net.dinkla.raytracer.gui

import java.text.SimpleDateFormat
import java.util.Date

object GuiUtilities {

    @JvmStatic
    fun getOutputPngFileName(fileName: String): String {
        var outFileName = fileName.replace(".[a-zA-Z0-9]+$".toRegex(), "")
        val df = SimpleDateFormat("yyyyMMddHHmmss")
        outFileName = "../" + df.format(Date()) + "_" + outFileName + ".png"
        return outFileName
    }

}
