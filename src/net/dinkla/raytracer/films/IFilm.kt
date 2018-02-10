package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

interface IFilm {

    open val resolution: Resolution

    fun initialize(numFrames: Int, resolution: Resolution)
    fun finish()  // finalize is already present in Java

    //void setPixel(int frame, int x, int y, int rgb);
    fun setPixel(frame: Int, x: Int, y: Int, color: Color)

    fun setBlock(frame: Int, x: Int, y: Int, width: Int, height: Int, color: Color)
}