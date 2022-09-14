package net.dinkla.raytracer.gui.awt

import java.awt.image.RenderedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

object Png {

    fun save(img: RenderedImage, fileName: String) {
        val file = File(fileName)
        try {
            ImageIO.write(img, "png", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}