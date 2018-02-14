package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D

class PerfectSpecular : BRDF() {

    var kr: Double
    var cr: Color

    init {
        kr = 1.0
        cr = Color.WHITE
    }

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color {
        throw RuntimeException("PerfectSpecular.f")
        // Im C-Code Black
    }

    override fun sampleF(sr: Shade, wo: Vector3D): BRDF.Sample {
        val result = newSample()
        val normal = sr.normal
        val nDotWo = normal dot wo
        val wi = wo.negate() + (sr.normal * (2.0 * nDotWo))
        result.wi = wi
        val nDotWi = normal dot wi
        result.color = cr * (kr / Math.abs(nDotWi))
        return result
    }

    override fun rho(sr: Shade, wo: Vector3D): Color {
        throw RuntimeException("PerfectSpecular.rho")
    }

}
