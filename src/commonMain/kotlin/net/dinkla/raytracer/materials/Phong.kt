package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.GlossySpecular
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.colors.ColorAccumulator
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.utilities.hash
import net.dinkla.raytracer.world.IWorld

open class Phong(
    color: Color = Color.WHITE,
    ka: Double = 0.25,
    kd: Double = 0.75
) : Matte(color, ka, kd) {

    protected val specularBRDF = GlossySpecular()

    constructor(
        color: Color = Color.WHITE,
        ka: Double = 0.25,
        kd: Double = 0.75,
        exp: Double = 5.0,
        ks: Double = 0.25,
        cs: Color = Color.WHITE
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

    override fun shade(world: IWorld, sr: IShade): Color {
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
                    val l = light.L(world, sr)
                    L += (fd + fs) * l * nDotWi
                }
            }
        }
        return L
    }

    override fun areaLightShade(world: IWorld, sr: IShade): Color {
        val wo = -sr.ray.direction
        val L = getAmbientColor(world, sr, wo)
        val S = ColorAccumulator()
        for (light1 in world.lights) {
            if (light1 is AreaLight) {
                val ls = light1.getSamples(sr)
                for (sample in ls) {
                    val nDotWi = sample.wi!! dot sr.normal
                    if (nDotWi > 0) {
                        var inShadow = false
                        if (light1.shadows) {
                            val shadowRay = Ray(sr.hitPoint, sample.wi!!)
                            inShadow = light1.inShadow(world, shadowRay, sr, sample)
                        }
                        if (!inShadow) {
                            val fd = diffuseBRDF.f(sr, wo, sample.wi!!)
                            val fs = specularBRDF.f(sr, wo, sample.wi!!)
                            val l = light1.L(world, sr, sample)
                            val f1 = light1.G(sr, sample) / light1.pdf(sr)
                            val T = (fd + fs) * l * nDotWi * f1
                            S + T
                        }
                    }
                }
            }
        }
        return L + S.average
    }

    override fun getLe(sr: IShade): Color = specularBRDF.cs * (specularBRDF.ks)

    override fun equals(other: Any?): Boolean = this.equals<Phong>(other) { a, b ->
        a.ambientBRDF == b.ambientBRDF && a.diffuseBRDF == b.diffuseBRDF && a.specularBRDF == b.specularBRDF
    }

    override fun hashCode(): Int = hash(super.diffuseBRDF, super.ambientBRDF, specularBRDF)

    override fun toString() = "Phong(${super.toString()}, $specularBRDF)"
}
