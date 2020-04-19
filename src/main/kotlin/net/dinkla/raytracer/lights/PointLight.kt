package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.interfaces.Counter
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.world.World

class PointLight(val location: Point3D = Point3D.ORIGIN,
                 val ls: Double = 1.0,
                 val color: Color = Color.WHITE,
                 override val shadows: Boolean = true) : Light {

    override fun L(world: World, sr: Shade): Color = color * ls

    override fun getDirection(sr: Shade): Vector3D = Vector3D(location - (Vector3D(sr.hitPoint))).normalize()

    override fun inShadow(world: World, ray: Ray, sr: Shade): Boolean {
        val d = (location - ray.origin).length()
        return world.inShadow(ray, sr, d)
    }
}
