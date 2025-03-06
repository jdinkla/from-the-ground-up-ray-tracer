package net.dinkla.raytracer.world

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.tracers.Tracer

interface IWorld {
    var tracer: Tracer?
    val lights: List<Light>
    val ambientLight: Ambient
    var backgroundColor: Color

    fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean

    fun inShadow(
        ray: Ray,
        sr: IShade,
        d: Double,
    ): Boolean

    fun shouldStopRecursion(depth: Int): Boolean
}
