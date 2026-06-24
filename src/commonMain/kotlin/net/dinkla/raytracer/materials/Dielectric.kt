package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.FresnelReflector
import net.dinkla.raytracer.btdf.FresnelTransmitter
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.world.IWorld
import java.util.Objects
import kotlin.math.abs

/**
 * Physically based glass-like transparency (Suffern, *Ray Tracing from the Ground Up*, ch. 28).
 *
 * Unlike [Transparent] — which weights the reflected and transmitted rays by fixed `kr`/`kt`
 * coefficients — `Dielectric` weights them by the **Fresnel reflectance/transmittance** computed
 * from the indices of refraction ([FresnelReflector] / [FresnelTransmitter]). It also models:
 *
 * - **Total internal reflection (TIR):** when the angle of incidence exceeds the critical angle
 *   there is no transmitted ray and all energy is reflected.
 * - **Beer's-law colored attenuation:** radiance carried along a path *inside* the medium is
 *   attenuated by the inside filter colour [cfIn] raised to the path length, and outside by [cfOut].
 *   `cf^d == exp(d · ln cf)`, i.e. exponential attenuation with per-channel coefficient `-ln cf`.
 *
 * Like [Transparent], the material drives the recursion itself by calling back into
 * `world.tracer`, so neither the Whitted tracer nor any other material needs to change.
 *
 * Which filter colour applies to which ray is decided from the sign of `n·wi` (the reflected
 * direction): when it is negative the reflected ray points *into* the surface, meaning the ray we
 * are shading is *inside* the medium (exiting), so the reflected ray stays inside (attenuated by
 * [cfIn]) and the transmitted ray leaves (attenuated by [cfOut]); when positive the roles swap.
 */
class Dielectric(
    color: Color = Color.WHITE,
    ka: Double = 0.25,
    kd: Double = 0.75,
) : Phong(color, ka, kd) {
    private val fresnelBRDF = FresnelReflector()
    private val fresnelBTDF = FresnelTransmitter()

    /** Filter colour applied (per channel, raised to path length) to rays travelling inside the medium. */
    var cfIn: Color = Color.WHITE

    /** Filter colour applied (per channel, raised to path length) to rays travelling outside the medium. */
    var cfOut: Color = Color.WHITE

    /** Index of refraction on the inside of the medium (the side the surface normal points away from). */
    var iorIn: Double
        get() = fresnelBTDF.iorIn
        set(v) {
            fresnelBTDF.iorIn = v
            fresnelBRDF.iorIn = v
        }

    /** Index of refraction on the outside of the medium (typically 1.0 for air/vacuum). */
    var iorOut: Double
        get() = fresnelBTDF.iorOut
        set(v) {
            fresnelBTDF.iorOut = v
            fresnelBRDF.iorOut = v
        }

    override fun shade(
        world: IWorld,
        sr: IShade,
    ): Color = super.shade(world, sr) + fresnelContribution(world, sr)

    override fun areaLightShade(
        world: IWorld,
        sr: IShade,
    ): Color = super.areaLightShade(world, sr) + fresnelContribution(world, sr)

    /**
     * Path-tracing shade (Suffern ch. 28 §28.9). A dielectric in the path tracer carries no direct
     * (Phong) term — it returns only the Fresnel-weighted reflected + transmitted radiance, with total
     * internal reflection and Beer's-law colored attenuation, i.e. exactly the [fresnelContribution]
     * the Whitted [shade] already trusts (the global-illumination analogue, mirroring how
     * [net.dinkla.raytracer.materials.Reflective.pathShade] drops the direct term).
     *
     * The Beer's-law attenuation needs the traced path length, which the path tracer reports through
     * the [net.dinkla.raytracer.tracers.Tracer] `WrappedDouble` overload (see
     * [net.dinkla.raytracer.tracers.PathTrace]). This transport is what carries light refracted through
     * the object onto another surface — a refractive caustic, which only path tracing can render.
     */
    override fun pathShade(
        world: IWorld,
        sr: IShade,
    ): Color = fresnelContribution(world, sr)

    /**
     * The reflected + transmitted radiance (Fresnel-weighted, Beer's-law attenuated, with TIR
     * handling), independent of the direct-lighting model. Added on top of the Phong/area-light
     * direct term by [shade] / [areaLightShade].
     */
    private fun fresnelContribution(
        world: IWorld,
        sr: IShade,
    ): Color {
        var l = Color.BLACK

        val wo = -sr.ray.direction
        val reflected = fresnelBRDF.sampleF(sr, wo)
        val reflectedRay = Ray(sr.hitPoint, reflected.wi)
        val nDotWi = sr.normal dot reflected.wi

        if (fresnelBTDF.isTir(sr)) {
            // No transmitted ray: all energy reflects. The reflected ray travels inside the medium,
            // so attenuate it with the inside filter over its path length.
            val t = WrappedDouble.createMax()
            val cr = world.tracer?.trace(reflectedRay, t, sr.depth + 1) ?: Color.BLACK
            l += if (nDotWi < 0.0) attenuate(cfIn, t.value, cr) else attenuate(cfOut, t.value, cr)
        } else {
            val transmitted = fresnelBTDF.sampleF(sr, wo)
            val transmittedRay = Ray(sr.hitPoint, transmitted.wt)
            val nDotWt = sr.normal dot transmitted.wt

            val tReflected = WrappedDouble.createMax()
            val cReflected = world.tracer?.trace(reflectedRay, tReflected, sr.depth + 1) ?: Color.BLACK
            val tTransmitted = WrappedDouble.createMax()
            val cTransmitted = world.tracer?.trace(transmittedRay, tTransmitted, sr.depth + 1) ?: Color.BLACK

            val reflectedFilter: Color
            val transmittedFilter: Color
            if (nDotWi < 0.0) {
                // Shading a hit from inside the medium: reflected ray stays inside, transmitted leaves.
                reflectedFilter = cfIn
                transmittedFilter = cfOut
            } else {
                // Shading a hit from outside: reflected ray stays outside, transmitted enters the medium.
                reflectedFilter = cfOut
                transmittedFilter = cfIn
            }

            l += attenuate(reflectedFilter, tReflected.value, reflected.color * cReflected) * abs(nDotWi)
            l += attenuate(transmittedFilter, tTransmitted.value, transmitted.color * cTransmitted) * abs(nDotWt)
        }
        return l
    }

    private fun attenuate(
        filter: Color,
        distance: Double,
        radiance: Color,
    ): Color = filter.pow(distance) * radiance

    override fun equals(other: Any?): Boolean =
        this.equals<Dielectric>(other) { a, b ->
            a.fresnelBRDF == b.fresnelBRDF &&
                a.fresnelBTDF == b.fresnelBTDF &&
                a.cfIn == b.cfIn &&
                a.cfOut == b.cfOut &&
                a.ambientBRDF == b.ambientBRDF &&
                a.diffuseBRDF == b.diffuseBRDF &&
                a.specularBRDF == b.specularBRDF
        }

    override fun hashCode(): Int =
        Objects.hash(super.diffuseBRDF, super.ambientBRDF, specularBRDF, fresnelBRDF, fresnelBTDF, cfIn, cfOut)

    override fun toString() = "Dielectric(${super.toString()}, $fresnelBRDF, $fresnelBTDF, cfIn=$cfIn, cfOut=$cfOut)"
}
