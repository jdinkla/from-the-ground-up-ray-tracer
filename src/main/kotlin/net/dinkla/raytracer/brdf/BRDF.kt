package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler

abstract class BRDF {
    internal var sampler: Sampler? = null

    class Sample {
        var color: Color? = null
        var wi: Vector3D? = null
        var pdf: Double = 0.toDouble()
    }

    abstract fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color

    abstract fun sampleF(sr: Shade, wo: Vector3D): Sample

    abstract fun rho(sr: Shade, wo: Vector3D): Color
}
