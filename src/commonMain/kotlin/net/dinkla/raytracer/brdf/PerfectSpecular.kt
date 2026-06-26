package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.brdf.SamplingBRDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D
import kotlin.math.abs

/**
 * A perfect mirror BRDF: only [sampleF] is meaningful (the reflected direction is a delta function,
 * so there is no evaluable [BRDF.f] and no [ReflectanceBRDF.rho]). It is therefore a [SamplingBRDF]
 * only. See TASK-63.
 */
data class PerfectSpecular(
    var kr: Double = 1.0,
    var cr: Color = Color.WHITE,
) : SamplingBRDF {
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
}
