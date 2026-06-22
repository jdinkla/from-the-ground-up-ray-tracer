package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.GlossySpecular
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.colors.ColorAccumulator
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.textures.Texture
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.world.IWorld
import java.util.Objects

/**
 * A spatially-varying [Phong]: the diffuse colour is read from a [Texture] (via [SvMatte]) while the
 * specular highlight stays a constant colour, as in the book. Shading mirrors [Phong] exactly; only
 * the diffuse BRDF differs.
 *
 * Mirrors Suffern's `SV_Phong` (Ray Tracing from the Ground Up, ch. 29).
 */
open class SvPhong(
    texture: Texture,
    ka: Double = 0.25,
    kd: Double = 0.75,
) : SvMatte(texture, ka, kd) {
    protected val specularBRDF = GlossySpecular()

    var ks: Double
        get() = specularBRDF.ks
        set(v) {
            specularBRDF.ks = v
        }

    var cs: Color
        get() = specularBRDF.cs
        set(v) {
            specularBRDF.cs = v
        }

    open var exp: Double
        get() = specularBRDF.exp
        set(v) {
            specularBRDF.exp = v
        }

    override fun shade(
        world: IWorld,
        sr: IShade,
    ): Color {
        val wo = -sr.ray.direction
        var radiance = getAmbientColor(world, sr, wo)
        for (light in world.lights) {
            val wi = light.getDirection(sr)
            val nDotWi = sr.normal dot wi
            if (nDotWi > 0) {
                var inShadow = false
                if (light.shadows) {
                    val shadowRay = Ray(sr.hitPoint, wi)
                    inShadow = light.inShadow(world, shadowRay, sr)
                }
                if (!inShadow) {
                    val fd = diffuseBRDF.f(sr, wo, wi)
                    val fs = specularBRDF.f(sr, wo, wi)
                    val l = light.l(world, sr)
                    radiance += (fd + fs) * l * nDotWi
                }
            }
        }
        return radiance
    }

    override fun areaLightShade(
        world: IWorld,
        sr: IShade,
    ): Color {
        val wo = -sr.ray.direction
        val radiance = getAmbientColor(world, sr, wo)
        val accumulator = ColorAccumulator()
        for (light in world.lights.filterIsInstance<AreaLight>()) {
            for (sample in light.getSamples(sr)) {
                val wi = requireNotNull(sample.wi) { "Sample.wi not set; call getSamples first" }
                val nDotWi = wi dot sr.normal
                if (nDotWi > 0) {
                    var inShadow = false
                    if (light.shadows) {
                        val shadowRay = Ray(sr.hitPoint, wi)
                        inShadow = light.inShadow(world, shadowRay, sr, sample)
                    }
                    if (!inShadow) {
                        val fd = diffuseBRDF.f(sr, wo, wi)
                        val fs = specularBRDF.f(sr, wo, wi)
                        val l = light.l(world, sr, sample)
                        val f1 = light.G(sr, sample) / light.pdf(sr)
                        accumulator + (fd + fs) * l * nDotWi * f1
                    }
                }
            }
        }
        return radiance + accumulator.average
    }

    override fun getLe(sr: IShade): Color = specularBRDF.cs * specularBRDF.ks

    override fun equals(other: Any?): Boolean =
        this.equals<SvPhong>(other) { a, b ->
            a.ambientBRDF == b.ambientBRDF && a.diffuseBRDF == b.diffuseBRDF && a.specularBRDF == b.specularBRDF
        }

    override fun hashCode(): Int = Objects.hash(ambientBRDF, diffuseBRDF, specularBRDF)

    override fun toString(): String = "SvPhong(${super.toString()}, $specularBRDF)"
}
