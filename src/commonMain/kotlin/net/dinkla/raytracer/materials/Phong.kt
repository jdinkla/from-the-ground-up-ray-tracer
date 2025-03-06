package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.GlossySpecular
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.colors.ColorAccumulator
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.world.IWorld
import java.util.Objects

open class Phong(
    color: Color = Color.WHITE,
    ka: Double = 0.25,
    kd: Double = 0.75,
) : Matte(color, ka, kd) {
    protected val specularBRDF = GlossySpecular()

    constructor(
        color: Color = Color.WHITE,
        ka: Double = 0.25,
        kd: Double = 0.75,
        exp: Double = 5.0,
        ks: Double = 0.25,
        cs: Color = Color.WHITE,
    ) : this(color, ka, kd) {
        this.exp = exp
        this.ks = ks
        this.cs = cs
    }

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
        var L = getAmbientColor(world, sr, wo)
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
                    L += (fd + fs) * l * nDotWi
                }
            }
        }
        return L
    }

    override fun areaLightShade(
        world: IWorld,
        sr: IShade,
    ): Color {
        val wo = -sr.ray.direction
        val L = getAmbientColor(world, sr, wo)
        val S = ColorAccumulator()
        for (light in world.lights.filterIsInstance<AreaLight>()) {
            val ls = light.getSamples(sr)
            for (sample in ls) {
                val nDotWi = sample.wi!! dot sr.normal
                if (nDotWi > 0) {
                    var inShadow = false
                    if (light.shadows) {
                        val shadowRay = Ray(sr.hitPoint, sample.wi!!)
                        inShadow = light.inShadow(world, shadowRay, sr, sample)
                    }
                    if (!inShadow) {
                        val fd = diffuseBRDF.f(sr, wo, sample.wi!!)
                        val fs = specularBRDF.f(sr, wo, sample.wi!!)
                        val l = light.l(world, sr, sample)
                        val f1 = light.G(sr, sample) / light.pdf(sr)
                        val T = (fd + fs) * l * nDotWi * f1
                        S + T
                    }
                }
            }
        }
        return L + S.average
    }

    override fun getLe(sr: IShade): Color = specularBRDF.cs * (specularBRDF.ks)

    override fun equals(other: Any?): Boolean =
        this.equals<Phong>(other) { a, b ->
            a.ambientBRDF == b.ambientBRDF && a.diffuseBRDF == b.diffuseBRDF && a.specularBRDF == b.specularBRDF
        }

    override fun hashCode(): Int = Objects.hash(super.diffuseBRDF, super.ambientBRDF, specularBRDF)

    override fun toString() = "Phong(${super.toString()}, $specularBRDF)"
}
