package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.SVLambertian
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.colors.ColorAccumulator
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.textures.Texture
import net.dinkla.raytracer.world.IWorld

open class SVMatte : IMaterial {

    var ambientBrdf: SVLambertian
    var diffuseBrdf: SVLambertian

    constructor() {
        ambientBrdf = SVLambertian()
        diffuseBrdf = SVLambertian()
        setKa(0.25)
        setKd(0.75)
        setCd(null)
    }

    constructor(color: Texture, ka: Double, kd: Double) {
        ambientBrdf = SVLambertian()
        diffuseBrdf = SVLambertian()
        setKa(ka)
        setKd(kd)
        setCd(color)
    }

    fun setKa(ka: Double) {
        ambientBrdf.kd = ka
    }

    fun setKd(kd: Double) {
        diffuseBrdf.kd = kd
    }

    fun setCd(cd: Texture?) {
        ambientBrdf.cd = cd
        diffuseBrdf.cd = cd
    }

    override fun shade(world: IWorld, sr: IShade): Color {
        val wo = -sr.ray.direction
        var L = getAmbientColor(world, sr, wo)
        for (light in world.lights) {
            val wi = light.getDirection(sr)
            val nDotWi = wi.dot(sr.normal)
            if (nDotWi > 0) {
                var inShadow = false
                if (light.shadows) {
                    val shadowRay = Ray(sr.hitPoint, wi)
                    inShadow = light.inShadow(world, shadowRay, sr)
                }
                if (!inShadow) {
                    val f = diffuseBrdf.f(sr, wo, wi)
                    val l = light.L(world, sr)
                    val flndotwi = f.times(l).times(nDotWi)
                    L = L.plus(flndotwi)
                }
            }
        }
        return L
    }

    override fun areaLightShade(world: IWorld, sr: IShade): Color {
        val wo = -sr.ray.direction
        var L = getAmbientColor(world, sr, wo)
        val S = ColorAccumulator()
        for (light1 in world.lights) {
            if (light1 is AreaLight) {
                val ls = light1.getSamples(sr)
                for (sample in ls) {
                    val nDotWi = sample.wi!!.dot(sr.normal)
                    if (nDotWi > 0) {
                        var inShadow = false
                        if (light1.shadows) {
                            val shadowRay = Ray(sr.hitPoint, sample.wi!!)
                            inShadow = light1.inShadow(world, shadowRay, sr, sample)
                        }
                        if (!inShadow) {
                            val f = diffuseBrdf.f(sr, wo, sample.wi!!)
                            val l = light1.L(world, sr, sample)
                            val flndotwi = f.times(l).times(nDotWi)
                            // TODO: hier ist der Unterschied zu shade()
                            val f1 = light1.G(sr, sample) / light1.pdf(sr)
                            val T = flndotwi.times(f1)
                            S.plus(T)
                        }
                    }
                }
            }
        }
        L = L.plus(S.average)
        return L
    }

    protected fun getAmbientColor(world: IWorld, sr: IShade, wo: Vector3D): Color {
        val c1 = ambientBrdf.rho(sr, wo)
        val c2 = world.ambientLight.L(world, sr)
        return c1.times(c2)
    }

    override fun getLe(sr: IShade): Color {
        return diffuseBrdf.rho(sr, null!!)
    }
}
