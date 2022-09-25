package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.world.IWorld

data class PointLight(
    val location: Point3D = Point3D.ORIGIN,
    val ls: Double = 1.0,
    val color: Color = Color.WHITE,
    override val shadows: Boolean = true
) : Light {
    override fun L(world: IWorld, sr: IShade): Color = color * ls
    override fun getDirection(sr: IShade): Vector3D = Vector3D(location - Vector3D(sr.hitPoint)).normalize()
    override fun inShadow(world: IWorld, ray: Ray, sr: IShade): Boolean =
        world.inShadow(ray, sr, (location - ray.origin).length)
}
