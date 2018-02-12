package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.worlds.World


class PointLight(var location: Point3D) : Light() {

    // emissive material
    var ls: Double = 0.toDouble()
    var color: Color? = null

    protected var cachedL: Color? = null

    init {
        color = Color.WHITE
        ls = 1.0
        shadows = true
    }

    override fun L(world: World, sr: Shade): Color {
        assert(null != color)
        if (null == cachedL) {
            cachedL = color!!.times(ls)
        }
        return cachedL ?: Color.BLACK
    }

    override fun getDirection(sr: Shade): Vector3D {
        return Vector3D(location.minus(Vector3D(sr.hitPoint))).normalize()
    }

    override fun inShadow(world: World, ray: Ray, sr: Shade): Boolean {
        val d = location.minus(ray.origin).length()
        return world.inShadow(ray, sr, d)
    }

}
