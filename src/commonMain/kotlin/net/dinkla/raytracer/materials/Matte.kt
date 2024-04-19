package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.Lambertian
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.colors.ColorAccumulator
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.world.IWorld
import java.util.Objects

open class Matte(color: Color = Color.WHITE, ka: Double = 0.25, kd: Double = 0.75) : IMaterial {

    protected val ambientBRDF = Lambertian(ka, color)
    protected val diffuseBRDF = Lambertian(kd, color)

    var ka: Double
        get() = ambientBRDF.kd
        set(v) {
            ambientBRDF.kd = v
        }

    var kd: Double
        get() = diffuseBRDF.kd
        set(v) {
            diffuseBRDF.kd = v
        }

    var cd: Color
        get() = ambientBRDF.cd
        set(v) {
            ambientBRDF.cd = v
            diffuseBRDF.cd = v
        }

    override fun shade(world: IWorld, sr: IShade): Color {
        val wo = -sr.ray.direction
        var L = getAmbientColor(world, sr, wo)
        for (light in world.lights) {
            val wi = light.getDirection(sr)
            val nDotWi = wi dot sr.normal
            if (nDotWi > 0) {
                var inShadow = false
                if (light.shadows) {
                    val shadowRay = Ray(sr.hitPoint, wi)
                    inShadow = light.inShadow(world, shadowRay, sr)
                }
                if (!inShadow) {
                    val f = diffuseBRDF.f(sr, wo, wi)
                    val l = light.l(world, sr)
                    L += (f * l) * nDotWi
                }
            }
        }
        return L
    }

    override fun areaLightShade(world: IWorld, sr: IShade): Color {
        val wo = -sr.ray.direction
        val L = getAmbientColor(world, sr, wo)
        val S = ColorAccumulator()
        for (light in world.lights) {
            if (light is AreaLight) {
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
                            val f = diffuseBRDF.f(sr, wo, sample.wi!!)
                            val l = light.l(world, sr, sample)
                            val f1 = light.G(sr, sample) / light.pdf(sr)
                            val T = (f * l) * nDotWi * f1
                            S + T
                        }
                    }
                }
            }
        }
        return L + S.average
    }

    protected fun getAmbientColor(world: IWorld, sr: IShade, wo: Vector3D): Color {
        val c1 = ambientBRDF.rho(sr, wo)
        val c2 = world.ambientLight.l(world, sr)
        return c1 * c2
    }

    override fun getLe(sr: IShade): Color {
        return diffuseBRDF.rho(sr, Vector3D.UP)
    }

    override fun equals(other: Any?): Boolean = this.equals<Matte>(other) { a, b ->
        a.ambientBRDF == b.ambientBRDF && a.diffuseBRDF == b.diffuseBRDF
    }

    override fun hashCode(): Int = Objects.hash(ambientBRDF, diffuseBRDF)

    override fun toString(): String = "Matte($ambientBRDF,$diffuseBRDF)"
}
