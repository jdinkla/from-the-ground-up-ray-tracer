package net.dinkla.raytracer.materials

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.brdf.PerfectSpecular
import net.dinkla.raytracer.btdf.PerfectTransmitter
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.worlds.World


class Transparent : Phong() {

    var reflectiveBrdf: PerfectSpecular
    var specularBtdf: PerfectTransmitter

    init {
        this.reflectiveBrdf = PerfectSpecular()
        this.specularBtdf = PerfectTransmitter()
    }

    fun setKt(kt: Double) {
        specularBtdf.kt = kt
    }

    fun setIor(ior: Double) {
        specularBtdf.ior = ior
    }

    fun setKr(kr: Double) {
        reflectiveBrdf.kr = kr
    }

    fun setCr(cr: Color) {
        reflectiveBrdf.cr = cr
    }

    override fun shade(world: World, sr: Shade): Color {
        var l = super.shade(world, sr)
        val wo = sr.ray.direction.times(-1.0)
        val brdf = reflectiveBrdf.sampleF(sr, wo)
        // trace reflected ray
        val reflectedRay = Ray(sr.hitPoint, brdf.wi!!)
        val cr = world.tracer.trace(reflectedRay, sr.depth + 1)
        if (specularBtdf.isTir(sr)) {
            l = l.plus(cr)
        } else {
            // reflected
            val cfr = Math.abs(sr.normal.dot(brdf.wi!!))
            l = l.plus(brdf.color!!.times(cr).times(cfr))

            // trace transmitted ray
            val btdf = specularBtdf.sampleF(sr, wo)
            val transmittedRay = Ray(sr.hitPoint, btdf.wt!!)
            val ct = world.tracer.trace(transmittedRay, sr.depth + 1)
            val cft = Math.abs(sr.normal.dot(btdf.wt!!))
            l = l.plus(btdf.color!!.times(ct).times(cft))
        }
        return l
    }
}
