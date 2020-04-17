package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.PerfectSpecular
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.utilities.hash
import net.dinkla.raytracer.world.World

class Reflective(color: Color = Color.WHITE,
                 ka: Double = 0.25,
                 kd: Double = 0.75) : Phong(color, ka, kd) {

    private val reflectiveBRDF = PerfectSpecular()

    var kr: Double
        get() = reflectiveBRDF.kr
        set(v: Double) {
            reflectiveBRDF.kr = v
        }

    var cr: Color
        get() = reflectiveBRDF.cr
        set(v: Color) {
            reflectiveBRDF.cr = v
        }

    override fun shade(world: World, sr: Shade): Color {
        val L = super.shade(world, sr)
        val wo = -sr.ray.direction
        val sample = reflectiveBRDF.sampleF(sr, wo)
        val f = sr.normal dot sample.wi
        val reflectedRay = Ray(sr.hitPoint, sample.wi)
        val c1 = world.tracer.trace(reflectedRay, sr.depth + 1)
        val c2 = sample.color * (c1).times(f)
        return L + c2
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is Reflective) {
            return super.equals(other) && reflectiveBRDF == other.reflectiveBRDF
        }
        return false
    }

    override fun hashCode(): Int = hash(reflectiveBRDF, ambientBRDF, diffuseBRDF, specularBRDF)

    override fun toString() = "Reflective $reflectiveBRDF ${super.toString()}"
}
