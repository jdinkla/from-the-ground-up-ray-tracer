package net.dinkla.raytracer.films

import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution


class JavaFxFilm : IFilm {

    val img: WritableImage

    private val pw: PixelWriter

    override val resolution: Resolution

    constructor(resolution: Resolution) {
        this.resolution = resolution
        img = WritableImage(resolution.hres, resolution.vres)
        pw = img.pixelWriter
    }

    inline fun transform(color: Color) = javafx.scene.paint.Color.color(color.red, color.green, color.blue)

    override fun setPixel(x: Int, y: Int, color: Color) = pw.setColor(x, y, transform(color))

    override fun setBlock(x: Int, y: Int, width: Int, height: Int, color: Color) {
        val c = transform(color)
        for (i in 0 until width) {
            for (j in 0 until height) {
                pw.setColor(x + i, y + j, c)
            }
        }
    }

    override fun saveAsPng(fileName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}