package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution
import java.awt.image.BufferedImage

interface Film {
    val resolution: Resolution
    val image : BufferedImage
    fun setPixel(x: Int, y: Int, color: Color)
    fun setBlock(x: Int, y: Int, width: Int, height: Int, color: Color)
}