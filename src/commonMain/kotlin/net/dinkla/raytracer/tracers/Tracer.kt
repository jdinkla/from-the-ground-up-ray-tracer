package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedDouble

/**
 * A ray-tracing strategy: computes the [Color] returned along a ray. Implementations realise the
 * different shading models from Suffern's *Ray Tracing from the Ground Up* —
 * [net.dinkla.raytracer.tracers.Whitted] (recursive reflection/refraction),
 * [net.dinkla.raytracer.tracers.AreaLighting] (area lights), and so on. The chosen strategy is
 * wired into the world by the render pipeline (see [net.dinkla.raytracer.world.Context]).
 */
interface Tracer {
    /**
     * Traces [ray] and returns its colour. [depth] is the current recursion level, used to bound
     * reflection/refraction so the trace terminates.
     */
    fun trace(
        ray: Ray,
        depth: Int,
    ): Color

    /**
     * Variant that also reports the hit distance through [tmin]. The default ignores [tmin] and
     * delegates to the two-argument [trace]; tracers that need to expose the nearest hit override it.
     */
    fun trace(
        ray: Ray,
        tmin: WrappedDouble,
        depth: Int,
    ): Color = trace(ray, depth)
}
