package net.dinkla.raytracer.cameras.render

import net.dinkla.raytracer.colors.Color

interface ISingleRayRenderer {

    fun render(r: Int, c: Int): Color

}
