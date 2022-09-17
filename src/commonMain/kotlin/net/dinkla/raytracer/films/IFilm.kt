package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

interface IFilm {
    val resolution: Resolution
    fun setPixel(x: Int, y: Int, color: Color)
}
