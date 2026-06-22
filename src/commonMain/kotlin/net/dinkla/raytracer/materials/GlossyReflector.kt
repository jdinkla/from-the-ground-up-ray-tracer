package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.GlossySpecular
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.world.IWorld
import java.util.Objects

/**
 * Glossy reflector (Suffern ch. 25, figure 25.1): a [Phong] surface that, on top of the direct
 * ambient/diffuse/specular shading, reflects its surroundings through a [GlossySpecular] BRDF.
 *
 * Where [Reflective] reflects along the single mirror direction, this material importance-samples the
 * reflected ray within a Phong lobe around that direction, so the reflection is blurred — the satin
 * look of the figure. [exp] is the glossiness: a high exponent gives a tight lobe (near-mirror
 * reflections), a low exponent a wide lobe (soft, blurred reflections); it also drives the Phong
 * highlight. [kr]/[cr] scale and tint the reflected radiance.
 *
 * Because the glossy reflection samples one random direction per ray, scenes need multi-sample
 * anti-aliasing (`samples(n)` in the DSL) to average out the per-pixel noise.
 */
class GlossyReflector(
    color: Color = Color.WHITE,
    ka: Double = 0.25,
    kd: Double = 0.75,
) : Phong(color, ka, kd) {
    private val glossySpecularBrdf = GlossySpecular()

    init {
        glossySpecularBrdf.setupSampler()
    }

    /** Reflection coefficient: how much of the glossy-reflected radiance is added. */
    var kr: Double
        get() = glossySpecularBrdf.ks
        set(v) {
            glossySpecularBrdf.ks = v
        }

    /** Reflection colour: tints the reflected radiance. */
    var cr: Color
        get() = glossySpecularBrdf.cs
        set(v) {
            glossySpecularBrdf.cs = v
        }

    /** Glossiness exponent: high = sharp/near-mirror, low = blurred/satin. Drives the lobe and highlight. */
    override var exp: Double
        get() = glossySpecularBrdf.exp
        set(v) {
            glossySpecularBrdf.exp = v
            super.exp = v
            glossySpecularBrdf.setupSampler()
        }

    override fun shade(
        world: IWorld,
        sr: IShade,
    ): Color = super.shade(world, sr) + glossyReflection(world, sr)

    override fun areaLightShade(
        world: IWorld,
        sr: IShade,
    ): Color = super.areaLightShade(world, sr) + glossyReflection(world, sr)

    /**
     * The glossy-reflection contribution: importance-sample a direction within the Phong lobe around
     * the mirror direction, trace it one level deeper, and weight by `color * (n . wi) / pdf` — which
     * reduces to `cr * kr * incoming`. Returns black when the sample faces away from the surface, has a
     * non-positive pdf, or there is no tracer.
     */
    private fun glossyReflection(
        world: IWorld,
        sr: IShade,
    ): Color {
        val wo = -sr.ray.direction
        val sample = glossySpecularBrdf.sampleF(sr, wo)
        val nDotWi = sr.normal dot sample.wi
        val tracer = world.tracer
        return if (nDotWi <= 0.0 || sample.pdf <= 0.0 || tracer == null) {
            Color.BLACK
        } else {
            val reflectedRay = Ray(sr.hitPoint, sample.wi)
            val incoming = tracer.trace(reflectedRay, sr.depth + 1)
            (sample.color * incoming) * (nDotWi / sample.pdf)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is GlossyReflector) {
            return super.equals(other) && glossySpecularBrdf == other.glossySpecularBrdf
        }
        return false
    }

    override fun hashCode(): Int = Objects.hash(glossySpecularBrdf, ambientBRDF, diffuseBRDF, specularBRDF)

    override fun toString() = "GlossyReflector $glossySpecularBrdf ${super.toString()}"
}
