package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.world.IWorld

interface Light {
    val shadows: Boolean

    fun l(
        world: IWorld,
        sr: IShade,
    ): Color

    fun getDirection(sr: IShade): Vector3D

    fun inShadow(
        world: IWorld,
        ray: Ray,
        sr: IShade,
    ): Boolean
}
