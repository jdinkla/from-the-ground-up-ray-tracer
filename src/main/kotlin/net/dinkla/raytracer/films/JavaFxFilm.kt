package net.dinkla.raytracer.films

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution
import java.awt.image.BufferedImage

class JavaFxFilm(override val resolution: Resolution) : Film {

    val img: WritableImage = WritableImage(resolution.hres, resolution.vres)

    private val pw: PixelWriter = img.pixelWriter

    override val image: BufferedImage
        get() = SwingFXUtils.fromFXImage(img, null)

    fun transform(color: Color) = javafx.scene.paint.Color.color(color.red, color.green, color.blue)

    override fun setPixel(x: Int, y: Int, color: Color)
            = pw.setColor(x, y, javafx.scene.paint.Color.color(color.red, color.green, color.blue))

    override fun setBlock(x: Int, y: Int, width: Int, height: Int, color: Color) {
        val c = transform(color)
        for (i in 0 until width) {
            for (j in 0 until height) {
                pw.setColor(x + i, y + j, c)
            }
        }
    }
}