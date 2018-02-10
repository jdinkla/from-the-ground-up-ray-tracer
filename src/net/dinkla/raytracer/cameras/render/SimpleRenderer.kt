package net.dinkla.raytracer.cameras.render

import net.dinkla.raytracer.cameras.lenses.ILens
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.tracers.Tracer

class SimpleRenderer(protected var lens: ILens, protected var tracer: Tracer) : ISingleRayRenderer {

    override fun render(r: Int, c: Int): Color {
        val ray = lens.getRaySingle(r, c)
        return tracer.trace(ray!!)
    }

}
