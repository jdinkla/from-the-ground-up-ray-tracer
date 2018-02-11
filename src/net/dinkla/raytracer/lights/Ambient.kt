package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.worlds.World

open class Ambient : Light() {

    // emissive material
    var ls: Double = 0.toDouble()
    var color: Color

    init {
        ls = 1.0
        color = Color.WHITE
    }

    override fun L(world: World, sr: Shade): Color {
        return color.times(ls)
    }

    override fun getDirection(sr: Shade): Vector3D {
        return Vector3D.ZERO
    }

    override fun inShadow(world: World, ray: Ray, sr: Shade): Boolean {
        return false
    }

}
