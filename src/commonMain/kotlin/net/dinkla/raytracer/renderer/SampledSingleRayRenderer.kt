package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.lenses.ILens
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.colors.ColorAccumulator
import net.dinkla.raytracer.samplers.NRooks
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.tracers.Tracer

/**
 * The multi-sample (anti-aliased) single-ray renderer: for each pixel it casts [numSamples] primary
 * rays, jittered within the pixel by [sampler]'s unit-square samples, and averages the traced
 * colours. This is the render path that makes view-plane anti-aliasing and thin-lens depth-of-field
 * blur visible (the [ThinLens][net.dinkla.raytracer.cameras.lenses.ThinLens] also jitters each ray's
 * origin across its aperture via [ILens.getRaySampled]). The single-sample, no-AA path is
 * [SimpleSingleRayRenderer].
 *
 * [numSamples] is the per-pixel sample count (driven by the scene's `ViewPlane.numSamples`); [sampler]
 * supplies the in-pixel jitter and must be built with a matching sample count.
 *
 * The default in-pixel sampler uses the [NRooks] generator deliberately: it produces exactly
 * `numSamples` stratified points per set for *any* positive sample count. The `sqrt`-based generators
 * (`MultiJittered`, `Jittered`, `Regular`) only generate `floor(sqrt(numSamples))²` points per set, so
 * `Sampler.sampleUnitSquare` indexes out of bounds for non-square sample counts (and `MultiJittered`
 * additionally throws whenever `numSets > sqrt(numSamples)` — the latent bug in this renderer's
 * original dead code, which hardcoded `Sampler(MultiJittered, 2500, 10)` and `numSamples = 1`).
 */
class SampledSingleRayRenderer(
    private val lens: ILens,
    private val tracer: Tracer,
    private val numSamples: Int,
    private val sampler: Sampler = Sampler(NRooks, numSamples, DEFAULT_NUM_SETS),
) : ISingleRayRenderer {
    init {
        require(numSamples > 0) { "numSamples must be positive, was $numSamples" }
    }

    override fun render(
        r: Int,
        c: Int,
    ): Color {
        val color = ColorAccumulator()
        for (j in 0 until numSamples) {
            val sp = sampler.sampleUnitSquare()
            // Skip samples that map to no valid ray (e.g. a FishEye sample outside the image circle):
            // the in-circle samples still contribute and `average` divides by their count, antialiasing
            // the image-circle edge. A pixel whose every sample is null averages to black (count == 0).
            val ray = lens.getRaySampled(r, c, sp) ?: continue
            color.plus(tracer.trace(ray, 0))
        }
        return color.average
    }

    companion object {
        /**
         * Default number of sample sets the in-pixel [Sampler] generates when none is supplied. More
         * than one set decorrelates the jitter pattern between neighbouring pixels (the renderer picks
         * a random set per pixel); the count is otherwise unconstrained for the [NRooks] generator.
         */
        const val DEFAULT_NUM_SETS: Int = 83
    }
}
