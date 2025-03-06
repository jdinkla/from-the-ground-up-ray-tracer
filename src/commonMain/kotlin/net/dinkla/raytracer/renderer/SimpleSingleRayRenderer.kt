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
        val ray = lens.getRaySingle(r, c)
        return tracer.trace(ray!!, 0)
    }
}
