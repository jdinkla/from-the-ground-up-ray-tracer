package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedDouble

interface Tracer {
    fun trace(
        ray: Ray,
        depth: Int,
    ): Color

    fun trace(
        ray: Ray,
        tmin: WrappedDouble,
        depth: Int,
    ): Color = trace(ray, depth)
}
