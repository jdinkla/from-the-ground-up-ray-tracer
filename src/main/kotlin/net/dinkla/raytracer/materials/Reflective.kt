package net.dinkla.raytracer.materials

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.brdf.PerfectSpecular
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.worlds.World

class Reflective : Phong() {

    internal var reflectiveBrdf: PerfectSpecular

    init {
        reflectiveBrdf = PerfectSpecular()
    }

    fun setKr(kr: Double) {
        reflectiveBrdf.kr = kr
    }

    fun setCr(cr: Color) {
        reflectiveBrdf.cr = cr
    }

    override fun shade(world: World, sr: Shade): Color {
        val L = super.shade(world, sr)
        val wo = -sr.ray.direction
        val sample = reflectiveBrdf.sampleF(sr, wo)
        val f = sr.normal.dot(sample.wi!!)
        val reflectedRay = Ray(sr.hitPoint, sample.wi!!)
        val c1 = world.tracer.trace(reflectedRay, sr.depth + 1)
        val c2 = sample.color!!.times(c1).times(f)
        return L.plus(c2)
    }

}
