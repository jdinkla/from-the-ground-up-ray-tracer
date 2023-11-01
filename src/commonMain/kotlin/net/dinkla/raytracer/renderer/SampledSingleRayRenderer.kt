package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.lenses.ILens
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.colors.ColorAccumulator
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.tracers.Tracer

class SampledSingleRayRenderer(var lens: ILens, var tracer: Tracer) : ISingleRayRenderer {
    // Used for anti-aliasing
    var sampler: Sampler
    var numSamples: Int = 0

    init {
        this.numSamples = 1
        this.sampler = Sampler(MultiJittered, 2500, 10)
    }

    override fun render(r: Int, c: Int): Color {
        val color = ColorAccumulator()
        for (j in 0 until numSamples) {
            val sp = sampler.sampleUnitSquare()
            val ray = lens.getRaySampled(r, c, sp)
            color.plus(tracer.trace(ray!!, 0))
        }
        return color.average
    }
}
