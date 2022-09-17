package net.dinkla.raytracer.gui.swing

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Resolution
import java.awt.image.BufferedImage

class SwingFilm(override val resolution: Resolution): IFilm {
    val image = BufferedImage(resolution.width, resolution.height, BufferedImage.TYPE_INT_RGB)
    override fun setPixel(x: Int, y: Int, color: Color) {
        image.setRGB(x, resolution.height - 1 - y, color.toInt())
    }
}