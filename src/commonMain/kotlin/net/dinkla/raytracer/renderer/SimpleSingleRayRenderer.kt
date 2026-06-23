package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.lenses.ILens
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.tracers.Tracer

class SimpleSingleRayRenderer(
    private var lens: ILens,
    private var tracer: Tracer,
) : ISingleRayRenderer {
    override fun render(
        r: Int,
        c: Int,
    ): Color {
        // A null ray means the pixel maps to no valid ray (e.g. a FishEye pixel outside the image
        // circle); per the ILens contract it is the background colour rather than an error.
        val ray = lens.getRaySingle(r, c) ?: return Color.BLACK
        return tracer.trace(ray, 0)
    }
}
