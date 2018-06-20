package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.gui.swing.ImageCanvas
import net.dinkla.raytracer.utilities.Resolution
import java.awt.Canvas
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class BufferedImageFilm : IFilm {

    private val img: BufferedImage

    override val resolution: Resolution

    val canvas: Canvas
        get() = ImageCanvas(img)

    constructor(resolution: Resolution) {
        this.resolution = resolution
        img = BufferedImage(resolution.hres, resolution.vres, BufferedImage.TYPE_INT_RGB)
    }

    override fun setPixel(x: Int, y: Int, color: Color) {
        assert(x >= 0)
        assert(x < resolution.hres)
        assert(y >= 0)
        assert(y < resolution.vres)
        img.setRGB(x, resolution.vres - 1 - y, color.asInt())
    }

    override fun setBlock(x: Int, y: Int, width: Int, height: Int, color: Color) {
        var pixel: Any? = null
        pixel = img.colorModel.getDataElements(color.asInt(), pixel)
        for (j in 0 until height) {
            for (i in 0 until width) {
                img.raster.setDataElements(x + i, resolution.vres - 1 - y - j, pixel)
            }
        }
    }

    override fun saveAsPng(fileName: String) {
        val file = File(fileName)
        try {
            ImageIO.write(img, "png", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
