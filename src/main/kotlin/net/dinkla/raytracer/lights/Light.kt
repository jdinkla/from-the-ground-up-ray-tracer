package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.world.World

abstract class Light {

    var shadows = true
    abstract fun L(world: World, sr: Shade): Color
    abstract fun getDirection(sr: Shade): Vector3D
    abstract fun inShadow(world: World, ray: Ray, sr: Shade): Boolean

}
