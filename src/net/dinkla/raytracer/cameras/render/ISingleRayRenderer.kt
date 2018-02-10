package net.dinkla.raytracer.cameras.render

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Ray

interface ISingleRayRenderer {

    fun render(r: Int, c: Int): Color

}
