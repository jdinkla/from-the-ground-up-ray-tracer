package net.dinkla.raytracer.gui.swing

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Resolution
import java.awt.image.BufferedImage

class SwingFilm(override val resolution: Resolution): IFilm {
    val image = BufferedImage(resolution.height, resolution.width, BufferedImage.TYPE_INT_RGB)
    override fun setPixel(x: Int, y: Int, color: Color) {
        assert(x >= 0)
        assert(x < resolution.height)
        assert(y >= 0)
        assert(y < resolution.width)
        image.setRGB(x, resolution.width - 1 - y, color.toInt())
    }
}