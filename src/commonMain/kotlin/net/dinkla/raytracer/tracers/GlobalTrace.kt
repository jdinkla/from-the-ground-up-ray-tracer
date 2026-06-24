package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.colors.ColorAccumulator
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.world.IWorld

/**
 * Hybrid global-illumination tracer (Suffern, *Ray Tracing from the Ground Up*, ch. 26, section 26.4).
 * Pure path tracing ([PathTrace]) is very noisy when the light sources are small, because few random
 * paths happen to hit a light (book Figs 26.7/26.10/26.12). [GlobalTrace] fixes this by splitting the
 * two transport terms:
 *
 *  - **direct** illumination is computed by *sampling the lights* at the first hit (`depth == 0`),
 *    exactly as the ch. 18 area-lighting tracer does ([net.dinkla.raytracer.materials.Matte.globalShade]
 *    delegates to [net.dinkla.raytracer.materials.Matte.areaLightShade]); and
 *  - **indirect** illumination is path-traced on the deeper bounces.
 *
 * The result is far less noisy than pure path tracing at the same sample count (book Fig 26.12). The
 * radiance-flow rules of Fig 26.11 keep the direct light from being counted twice; they live in the
 * materials' `globalShade` methods (Listings 26.6–26.8), so this tracer differs from [PathTrace] only
 * in that it calls `globalShade` instead of `pathShade`.
 *
 * **Per-pixel averaging.** As with [PathTrace], the render pipeline wires a single primary ray per
 * pixel with no anti-aliasing, so the tracer averages [numSamples] independent paths itself at the
 * **primary** level (`depth == 0`); deeper recursion levels trace a single bounce.
 */
class GlobalTrace(
    private val world: IWorld,
    private val numSamples: Int = DEFAULT_NUM_SAMPLES,
) : Tracer {
    override fun trace(
        ray: Ray,
        depth: Int,
    ): Color = trace(ray, WrappedDouble.createMax(), depth)

    /**
     * Variant that also reports the nearest hit distance through [tmin] (mirroring [PathTrace]). Kept
     * for symmetry with the other recursion-aware tracers so dielectric global shading can apply
     * Beer's-law attenuation along the traced path length.
     */
    override fun trace(
        ray: Ray,
        tmin: WrappedDouble,
        depth: Int,
    ): Color =
        if (depth == 0) {
            val acc = ColorAccumulator()
            repeat(numSamples) { acc.plus(traceGlobal(ray, tmin, depth)) }
            acc.average
        } else {
            traceGlobal(ray, tmin, depth)
        }

    /** Traces a single path: black past the recursion bound, otherwise the material's global shade. */
    private fun traceGlobal(
        ray: Ray,
        tmin: WrappedDouble,
        depth: Int,
    ): Color {
        if (world.shouldStopRecursion(depth)) return Color.BLACK
        val sr = Shade()
        return if (world.hit(ray, sr)) {
            sr.depth = depth
            sr.ray = ray
            tmin.value = sr.t
            sr.material?.globalShade(world, sr) ?: world.backgroundColor
        } else {
            tmin.value = MathUtils.K_HUGE_VALUE
            world.backgroundColor
        }
    }

    private companion object {
        /** Paths averaged per primary ray; a compromise between convergence and render time. */
        const val DEFAULT_NUM_SAMPLES = 100
    }
}
