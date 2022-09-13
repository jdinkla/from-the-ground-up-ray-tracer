package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.brdf.BRDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D
import kotlin.math.abs
import net.dinkla.raytracer.interfaces.hash

class PerfectSpecular(var kr: Double = 1.0, var cr: Color = Color.WHITE) : BRDF {

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color {
        throw RuntimeException("PerfectSpecular.f")
        // TODO Im C-Code Black
    }

    override fun sampleF(sr: Shade, wo: Vector3D): Sample {
        val normal = sr.normal
        val nDotWo = normal dot wo
        val wi = -wo + (sr.normal * (2.0 * nDotWo))
        val nDotWi = normal dot wi
        return Sample(wi = wi, color = cr * (kr / abs(nDotWi)), pdf = 1.0)
    }

    override fun rho(sr: Shade, wo: Vector3D): Color {
        throw RuntimeException("PerfectSpecular.rho")
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is PerfectSpecular) {
            return kr == other.kr && cr == other.cr
        }
        return false
    }

    override fun hashCode(): Int = hash(kr, cr)

    override fun toString() = "PerfectSpecular($kr, $cr)"
}
