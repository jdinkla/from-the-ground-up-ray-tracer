package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.PerfectSpecular
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.world.IWorld
import java.util.Objects

class Reflective(
    color: Color = Color.WHITE,
    ka: Double = 0.25,
    kd: Double = 0.75,
) : Phong(color, ka, kd) {
    private val reflectiveBRDF = PerfectSpecular()

    var kr: Double
        get() = reflectiveBRDF.kr
        set(v) {
            reflectiveBRDF.kr = v
        }

    var cr: Color
        get() = reflectiveBRDF.cr
        set(v) {
            reflectiveBRDF.cr = v
        }

    override fun shade(
        world: IWorld,
        sr: IShade,
    ): Color {
        val L = super.shade(world, sr)
        val wo = -sr.ray.direction
        val sample = reflectiveBRDF.sampleF(sr, wo)
        val f = sr.normal dot sample.wi
        val reflectedRay = Ray(sr.hitPoint, sample.wi)
        val c1 = world.tracer?.trace(reflectedRay, sr.depth + 1) ?: Color.WHITE
        val c2 = sample.color * c1 * f
        return L + c2
    }

    /**
     * Path-tracing shade (Suffern ch. 26, Listing 26.5): a mirror in the path tracer carries no direct
     * term — it samples the single perfect-specular direction ([PerfectSpecular.sampleF]), traces that
     * reflected ray one level deeper, and returns the incoming radiance weighted by `f * (n . wi)`.
     *
     * `sampleF` returns `color = cr * (kr / |n . wi|)` and `pdf = 1`, so for the reflected direction
     * (`n . wi > 0`) the weight collapses to `cr * kr * incoming` — the lobe geometry cancels and the
     * pdf is unit, unlike the cosine-weighted diffuse bounce in [Matte.pathShade]. Returns black when
     * the world has no tracer to recurse through.
     */
    override fun pathShade(
        world: IWorld,
        sr: IShade,
    ): Color {
        val wo = -sr.ray.direction
        val sample = reflectiveBRDF.sampleF(sr, wo)
        val nDotWi = sr.normal dot sample.wi
        val tracer = world.tracer ?: return Color.BLACK
        val reflectedRay = Ray(sr.hitPoint, sample.wi)
        val incoming = tracer.trace(reflectedRay, sr.depth + 1)
        return (sample.color * incoming) * nDotWi
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is Reflective) {
            return super.equals(other) && reflectiveBRDF == other.reflectiveBRDF
        }
        return false
    }

    override fun hashCode(): Int = Objects.hash(reflectiveBRDF, ambientBRDF, diffuseBRDF, specularBRDF)

    override fun toString() = "Reflective $reflectiveBRDF ${super.toString()}"
}
