package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.brdf.SamplingBRDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Reflective BRDF for a dielectric (glass-like) surface (Suffern, *Ray Tracing from the Ground Up*,
 * ch. 28). Unlike [PerfectSpecular], the reflectance is not a fixed coefficient but the angle- and
 * IOR-dependent **Fresnel reflectance** [fresnel]. The reflection direction is the mirror of `wo`
 * about the surface normal; the colour returned by [sampleF] is white scaled by `kr / |n·wi|` so the
 * `n·wi` factor in the caller's recursion cancels, exactly mirroring [PerfectSpecular].
 *
 * Like [PerfectSpecular] it is a [SamplingBRDF] only: a delta reflector has no evaluable `f` and no
 * bihemispherical reflectance `rho` (see TASK-63).
 *
 * [iorIn] is the index of refraction on the side the normal points to (the medium the ray came
 * from when entering), [iorOut] on the other side. Suffern's `FresnelReflector` carries both so the
 * same object serves rays entering and leaving the medium; which ratio applies is decided per hit
 * from the sign of `n·wo`.
 */
data class FresnelReflector(
    var iorIn: Double = 1.0,
    var iorOut: Double = 1.0,
) : SamplingBRDF {
    /**
     * The Fresnel reflectance `kr` at this hit: the fraction of energy reflected, averaged over the
     * two polarizations (Suffern's exact, non-Schlick form). At normal incidence this reduces to
     * `((eta-1)/(eta+1))^2`; it rises to 1.0 at grazing incidence and is 1.0 under total internal
     * reflection. `eta` is the relative index of refraction across the interface, oriented by the
     * sign of `n·wo` so the ray's own direction selects entering vs leaving.
     */
    fun fresnel(sr: IShade): Double {
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
        if (cosThetaTSqr < 0.0) {
            // Total internal reflection: all energy is reflected.
            return 1.0
        }
        val cosThetaT = sqrt(cosThetaTSqr)
        val rParallel = (eta * cosThetaI - cosThetaT) / (eta * cosThetaI + cosThetaT)
        val rPerpendicular = (cosThetaI - eta * cosThetaT) / (cosThetaI + eta * cosThetaT)
        return HALF * (rParallel * rParallel + rPerpendicular * rPerpendicular)
    }

    override fun sampleF(
        sr: IShade,
        wo: Vector3D,
    ): Sample {
        val normal = sr.normal
        val nDotWo = normal dot wo
        val wi = -wo + (normal * (2.0 * nDotWo))
        val kr = fresnel(sr)
        return Sample(wi = wi, color = Color.WHITE * (kr / abs(normal dot wi)), pdf = 1.0)
    }

    private companion object {
        /** Averages the parallel/perpendicular reflectances for unpolarized light. */
        private const val HALF = 0.5
    }
}
