package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.world.World

// emissive material

class PointLight(val location: Point3D = Point3D.ORIGIN,
                 val ls: Double = 1.0,
                 val color: Color = Color.WHITE) : Light() {

    private var cachedL: Color? = null

    init {
        shadows = true
    }

    override fun L(world: World, sr: Shade): Color {
        if (null == cachedL) {
            cachedL = color.times(ls)
        }
        return cachedL ?: Color.BLACK
    }

    override fun getDirection(sr: Shade): Vector3D {
        return Vector3D(location - (Vector3D(sr.hitPoint))).normalize()
    }

    override fun inShadow(world: World, ray: Ray, sr: Shade): Boolean {
        val d = (location - ray.origin).length()
        return world.inShadow(ray, sr, d)
    }

}
