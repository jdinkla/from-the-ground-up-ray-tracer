package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

expect class Film {
    val resolution: Resolution
    fun setPixel(x: Int, y: Int, color: Color)
    fun setBlock(x: Int, y: Int, width: Int, height: Int, color: Color)
    fun save(filename : String)
}