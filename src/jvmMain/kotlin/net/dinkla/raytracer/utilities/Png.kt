package net.dinkla.raytracer.utilities

import java.awt.image.RenderedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

fun RenderedImage.save(fileName: String) {
    val file = File(fileName)
    try {
        ImageIO.write(this, "png", file)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
