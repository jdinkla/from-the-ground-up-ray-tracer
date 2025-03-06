package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.brdf.BRDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D
import kotlin.math.abs

data class PerfectSpecular(
    var kr: Double = 1.0,
    var cr: Color = Color.WHITE,
) : BRDF {
    override fun f(
        sr: IShade,
        wo: Vector3D,
        wi: Vector3D,
    ): Color = throw RuntimeException("PerfectSpecular.f")

    override fun sampleF(
        sr: IShade,
        wo: Vector3D,
    ): Sample {
        val normal = sr.normal
        val nDotWo = normal dot wo
        val wi = -wo + (sr.normal * (2.0 * nDotWo))
        val nDotWi = normal dot wi
        return Sample(wi = wi, color = cr * (kr / abs(nDotWi)), pdf = 1.0)
    }

    override fun rho(
        sr: IShade,
        wo: Vector3D,
    ): Color = throw RuntimeException("PerfectSpecular.rho")
}
