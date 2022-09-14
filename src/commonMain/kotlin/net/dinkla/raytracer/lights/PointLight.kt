package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.utilities.hash
import net.dinkla.raytracer.world.IWorld

class PointLight(val location: Point3D = Point3D.ORIGIN,
                 val ls: Double = 1.0,
                 val color: Color = Color.WHITE,
                 override val shadows: Boolean = true) : Light {

    override fun L(world: IWorld, sr: IShade): Color = color * ls

    override fun getDirection(sr: IShade): Vector3D = Vector3D(location - Vector3D(sr.hitPoint)).normalize()

    override fun inShadow(world: IWorld, ray: Ray, sr: IShade): Boolean {
        val d = (location - ray.origin).length()
        return world.inShadow(ray, sr, d)
    }

    override fun equals(other: Any?): Boolean = this.equals<PointLight>(other) { a, b ->
        a.location == b.location && a.ls == b.ls && a.color == b.color && a.shadows == b.shadows
    }

    override fun hashCode(): Int = this.hash(location, ls, color, shadows)

    override fun toString(): String = "PointLight($location, $ls, $color, $shadows)"
}
