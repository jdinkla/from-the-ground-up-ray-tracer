package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.lenses.ILens
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.tracers.Tracer

/**
 * The single-sample (no anti-aliasing) single-ray renderer. [exposureTime] (default `1.0`) scales
 * the radiance returned for each primary ray — the camera's
 * [exposureTime][net.dinkla.raytracer.cameras.Camera.exposureTime]. At the default it is a no-op, so
 * existing scenes are byte-identical; a reduced value darkens every pixel uniformly (used for views
 * from inside a dense transparent medium, see [Camera][net.dinkla.raytracer.cameras.Camera]).
 */
class SimpleSingleRayRenderer(
    private var lens: ILens,
    private var tracer: Tracer,
    private val exposureTime: Double = 1.0,
) : ISingleRayRenderer {
    override fun render(
        r: Int,
        c: Int,
    ): Color {
        // A null ray means the pixel maps to no valid ray (e.g. a FishEye pixel outside the image
        // circle); per the ILens contract it is the background colour rather than an error.
        val ray = lens.getRaySingle(r, c) ?: return Color.BLACK
        return tracer.trace(ray, 0) * exposureTime
    }
}
