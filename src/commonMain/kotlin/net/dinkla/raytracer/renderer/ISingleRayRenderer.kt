package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.colors.Color

interface ISingleRayRenderer {

    fun render(r: Int, c: Int): Color
}
