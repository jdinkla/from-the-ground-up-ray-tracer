package net.dinkla.raytracer.btdf

import net.dinkla.raytracer.btdf.BTDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Transmissive BTDF for a dielectric (glass-like) surface (Suffern, *Ray Tracing from the Ground
 * Up*, ch. 28). Mirrors [PerfectTransmitter], but the transmittance is the **Fresnel**
 * transmittance (`kt = 1 - kr`, energy conservation) computed from the indices of refraction rather
 * than a fixed coefficient, and total internal reflection is reported through [isTir].
 *
 * The transmitted direction follows Snell's law. [iorIn] is the index of refraction on the side the
 * normal points to, [iorOut] on the other side; the relative index `eta` is oriented per hit by the
 * sign of `n·wo` so the ray's direction selects entering vs leaving the medium.
 */
data class FresnelTransmitter(
    var iorIn: Double = 1.0,
    var iorOut: Double = 1.0,
) : BTDF {
    /**
     * The Fresnel transmittance `kt = 1 - kr` and the transmitted direction. The caller must not
     * invoke this when [isTir] is true: under total internal reflection there is no transmitted ray
     * (the radicand below would be negative).
     */
    override fun sampleF(
        sr: IShade,
        wo: Vector3D,
    ): Sample {
        var normal = sr.normal
        var cosThetaI = normal dot wo
        var eta = iorIn / iorOut
        if (cosThetaI < 0.0) {
            cosThetaI = -cosThetaI
            normal = -normal
            eta = 1.0 / eta
        }
        val cosThetaTSqr = 1.0 - (1.0 - cosThetaI * cosThetaI) / (eta * eta)
        val cosThetaT = sqrt(cosThetaTSqr)
        val wt = (wo * -1.0) * (1.0 / eta) - normal * (cosThetaT - cosThetaI / eta)
        val kt = 1.0 - fresnel(sr)
        return Sample(wt = wt, color = Color.WHITE * (kt / (eta * eta) / abs(sr.normal dot wt)))
    }

    /** True when the angle of incidence exceeds the critical angle, so all energy reflects. */
    override fun isTir(sr: IShade): Boolean {
        val wo = -sr.ray.direction
        val cosThetaI = sr.normal dot wo
        val eta = if (cosThetaI < 0.0) iorOut / iorIn else iorIn / iorOut
        val cosThetaTSqr = 1.0 - (1.0 - cosThetaI * cosThetaI) / (eta * eta)
        return cosThetaTSqr < 0.0
    }

    /**
     * The Fresnel reflectance `kr` at this hit — the same exact, unpolarized average used by
     * `FresnelReflector`. Duplicated here (rather than shared) so the transmitter is self-contained,
     * matching Suffern's design where each BTDF/BRDF computes Fresnel from its own IORs.
     */
    private fun fresnel(sr: IShade): Double {
        val normal = sr.normal
        var cosThetaI = normal dot -sr.ray.direction
        val eta =
            if (cosThetaI < 0.0) {
                cosThetaI = -cosThetaI
                iorOut / iorIn
            } else {
                iorIn / iorOut
            }
        val cosThetaTSqr = 1.0 - (1.0 - cosThetaI * cosThetaI) / (eta * eta)
        val cosThetaT = sqrt(cosThetaTSqr)
        val rParallel = (eta * cosThetaI - cosThetaT) / (eta * cosThetaI + cosThetaT)
        val rPerpendicular = (cosThetaI - eta * cosThetaT) / (cosThetaI + eta * cosThetaT)
        return HALF * (rParallel * rParallel + rPerpendicular * rPerpendicular)
    }

    private companion object {
        /** Averages the parallel/perpendicular reflectances for unpolarized light. */
        private const val HALF = 0.5
    }
}
