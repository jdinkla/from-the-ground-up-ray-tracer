package net.dinkla.raytracer.btdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler

abstract class BTDF {
    internal var sampler: Sampler? = null

    class Sample {
        var color: Color? = null
        var wt: Vector3D? = null
    }

    abstract fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color
    abstract fun sampleF(sr: Shade, wo: Vector3D): Sample
    abstract fun rho(sr: Shade, wo: Vector3D): Color
    abstract fun isTir(sr: Shade): Boolean
}
