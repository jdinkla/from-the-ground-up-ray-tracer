package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.SVGlossySpecular
import net.dinkla.raytracer.colors.ColorAccumulator
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.textures.Texture
import net.dinkla.raytracer.worlds.World

class SVPhong : SVMatte() {

    var specularBrdf: SVGlossySpecular

    init {
        specularBrdf = SVGlossySpecular()
    }

    fun setKs(ks: Double) {
        specularBrdf.ks = ks
    }

    fun setExp(exp: Double) {
        specularBrdf.exp = exp
    }

    fun setCs(cs: Texture) {
        specularBrdf.cs = cs
    }

    override fun shade(world: World, sr: Shade): Color {
        val wo = sr.ray.d.negate()
        var L = getAmbientColor(world, sr, wo)
        for (light in world.lights) {
            val wi = light.getDirection(sr)
            val nDotWi = sr.normal.dot(wi)
            if (nDotWi > 0) {
                var inShadow = false
                if (light.shadows) {
                    val shadowRay = Ray(sr.hitPoint, wi)
                    inShadow = light.inShadow(world, shadowRay, sr)
                }
                if (!inShadow) {
                    val fd = diffuseBrdf.f(sr, wo, wi)
                    val fs = specularBrdf.f(sr, wo, wi)
                    val l = light.L(world, sr)
                    val fdfslndotwi = fd.plus(fs).times(l).times(nDotWi)
                    L = L.plus(fdfslndotwi)
                }
            }
        }
        return L
    }


    override fun areaLightShade(world: World, sr: Shade): Color {
        val wo = sr.ray.d.negate()
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
                            val fd = diffuseBrdf.f(sr, wo, sample.wi!!)
                            val fs = specularBrdf.f(sr, wo, sample.wi!!)
                            val l = light1.L(world, sr, sample)
                            val fsfslndotwi = fd.plus(fs).times(l).times(nDotWi)
                            // TODO: hier ist der Unterschied zu shade()
                            val f1 = light1.G(sr, sample) / light1.pdf(sr)
                            val T = fsfslndotwi.times(f1)
                            S.plus(T)
                        }
                    }
                }
            }
        }
        L = L.plus(S.average)
        return L
    }

    override fun getLe(sr: Shade): Color {
        // TODO
        return specularBrdf.cs!!.getColor(sr).times(specularBrdf.ks)
    }
}

