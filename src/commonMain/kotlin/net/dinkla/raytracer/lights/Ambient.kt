package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.world.IWorld

// emissive material
open class Ambient(
    var ls: Double = 1.0,
    var color: Color = Color.WHITE,
    override val shadows: Boolean = true
) : Light {

    override fun L(world: IWorld, sr: IShade): Color = color * ls

    override fun getDirection(sr: IShade) = Vector3D.ZERO

    override fun inShadow(world: IWorld, ray: Ray, sr: IShade): Boolean = false
}
