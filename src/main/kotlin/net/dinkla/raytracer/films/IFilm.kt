package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

interface IFilm {

    open val resolution: Resolution

    fun setPixel(x: Int, y: Int, color: Color)

    fun setBlock(x: Int, y: Int, width: Int, height: Int, color: Color)

    fun saveAsPng(fileName: String)

}