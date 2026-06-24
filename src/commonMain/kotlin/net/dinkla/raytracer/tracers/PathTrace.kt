package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.colors.ColorAccumulator
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.world.IWorld

/**
 * Monte Carlo path tracer (Suffern, *Ray Tracing from the Ground Up*, ch. 26). It estimates global
 * illumination by recursively sampling indirect bounces: at each hit the material's
 * [net.dinkla.raytracer.materials.IMaterial.pathShade] spawns an importance-sampled diffuse bounce
 * (cosine-weighted, see [net.dinkla.raytracer.brdf.Lambertian.sampleF]) and emissive surfaces act as
 * the only light sources. Repeatedly averaging such random paths converges to the rendering-equation
 * solution, giving colour bleeding, soft indirect lighting and soft shadows.
 *
 * **Per-pixel averaging.** The render pipeline wires a
 * [net.dinkla.raytracer.renderer.SimpleSingleRayRenderer], which shoots a single primary ray per
 * pixel with no anti-aliasing. A single Monte Carlo path per pixel would be extremely noisy, so the
 * path tracer does its own averaging at the **primary** level: when [trace] is entered at `depth`
 * `0` it averages [numSamples] independent paths; deeper recursion levels trace a single bounce. This
 * keeps the convergence sampling inside the tracer where it belongs and leaves the rest of the
 * pipeline untouched.
 */
class PathTrace(
    private val world: IWorld,
    private val numSamples: Int = DEFAULT_NUM_SAMPLES,
) : Tracer {
    override fun trace(
        ray: Ray,
        depth: Int,
    ): Color = trace(ray, WrappedDouble.createMax(), depth)

    /**
     * Variant that also reports the nearest hit distance through [tmin] (mirroring
     * [net.dinkla.raytracer.tracers.Whitted]). Dielectric path shading needs the traced path length to
     * apply Beer's-law colored attenuation, so the path tracer exposes it here rather than leaving the
     * inherited default that ignores [tmin].
     */
    override fun trace(
        ray: Ray,
        tmin: WrappedDouble,
        depth: Int,
    ): Color =
        if (depth == 0) {
            val acc = ColorAccumulator()
            repeat(numSamples) { acc.plus(tracePath(ray, tmin, depth)) }
            acc.average
        } else {
            tracePath(ray, tmin, depth)
        }

    /** Traces a single path: returns black past the recursion bound, otherwise the material's path shade. */
    private fun tracePath(
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
            sr.material?.pathShade(world, sr) ?: world.backgroundColor
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
