package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB

class BufferedImageFilm(override val resolution: Resolution) : Film {

    private val img = BufferedImage(resolution.hres, resolution.vres, TYPE_INT_RGB)

    override val image : BufferedImage
        get() = img

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
}
