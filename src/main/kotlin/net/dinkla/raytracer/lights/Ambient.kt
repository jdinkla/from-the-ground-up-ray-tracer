package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.worlds.World

// emissive material
open class Ambient(var ls: Double = 1.0, var color: Color = Color.WHITE) : Light() {

    override fun L(world: World, sr: Shade): Color = color * ls

    override fun getDirection(sr: Shade) = Vector3D.ZERO

    override fun inShadow(world: World, ray: Ray, sr: Shade): Boolean = false

}
