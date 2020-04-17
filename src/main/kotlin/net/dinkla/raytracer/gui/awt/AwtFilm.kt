package net.dinkla.raytracer.gui.awt

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.utilities.Resolution
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB

class AwtFilm(override val resolution: Resolution) : Film {

    override val image = BufferedImage(resolution.hres, resolution.vres, TYPE_INT_RGB)

    override fun setPixel(x: Int, y: Int, color: Color) {
        assert(x >= 0)
        assert(x < resolution.hres)
        assert(y >= 0)
        assert(y < resolution.vres)
        image.setRGB(x, resolution.vres - 1 - y, color.toInt())
    }

    override fun setBlock(x: Int, y: Int, width: Int, height: Int, color: Color) {
        var pixel: Any? = null
        pixel = image.colorModel.getDataElements(color.toInt(), pixel)
        for (j in 0 until height) {
            for (i in 0 until width) {
                image.raster.setDataElements(x + i, resolution.vres - 1 - y - j, pixel)
            }
        }
    }
}
