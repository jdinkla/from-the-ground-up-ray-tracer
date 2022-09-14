package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.PerfectSpecular
import net.dinkla.raytracer.btdf.PerfectTransmitter
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.utilities.hash
import net.dinkla.raytracer.world.IWorld
import kotlin.math.abs

class Transparent : Phong {

    private var reflectiveBRDF = PerfectSpecular()
    private var specularBTDF = PerfectTransmitter()

    constructor(color: Color = Color.WHITE,
                ka: Double = 0.25,
                kd: Double = 0.75,
                exp: Double = 5.0,
                ks: Double = 0.25,
                cs: Color = Color.WHITE,
                kt: Double = 0.0,
                ior: Double = 0.0,
                kr: Double = 0.0,
                cr: Color = Color.WHITE): super(color, ka, kd, exp, ks, cs) {
        this.kt = kt
        this.ior = ior
        this.kr = kr
        this.cr = cr
    }

    var kt: Double
        get() = specularBTDF.kt
        set(v: Double) {
            specularBTDF.kt = v
        }

    var ior: Double
        get() = specularBTDF.ior
        set(v: Double) {
            specularBTDF.ior = v
        }

    var kr: Double
        get() =reflectiveBRDF.kr
        set(v: Double) {
            reflectiveBRDF.kr = v
        }

    var cr: Color
        get() =reflectiveBRDF.cr
        set(v: Color) {
            reflectiveBRDF.cr = v
        }


    override fun shade(world: IWorld, sr: IShade): Color {
        var l = super.shade(world, sr)
        val wo = sr.ray.direction.times(-1.0)
        val brdf = reflectiveBRDF.sampleF(sr, wo)
        // trace reflected ray
        val reflectedRay = Ray(sr.hitPoint, brdf.wi)
        val cr = world.tracer?.trace(reflectedRay, sr.depth + 1) ?: Color.BLACK
        if (specularBTDF.isTir(sr)) {
            l += cr
        } else {
            // reflected
            val cfr = abs(sr.normal dot brdf.wi)
            l += (brdf.color * cr) * cfr

            // trace transmitted ray
            val btdf = specularBTDF.sampleF(sr, wo)
            val transmittedRay = Ray(sr.hitPoint, btdf.wt)
            val ct = world.tracer?.trace(transmittedRay, sr.depth + 1) ?: Color.WHITE
            val cft = abs(sr.normal dot btdf.wt)
            l += (btdf.color * ct) * cft
        }
        return l
    }

    override fun equals(other: Any?): Boolean = this.equals<Transparent>(other) { a, b ->
        a.diffuseBRDF == b.diffuseBRDF && a.ambientBRDF == b.ambientBRDF && a.specularBTDF == b.specularBTDF
                && a.reflectiveBRDF == b.reflectiveBRDF && a.specularBRDF == b.specularBRDF
    }

    override fun hashCode(): Int =
        hash(super.diffuseBRDF, super.ambientBRDF, specularBRDF, reflectiveBRDF, specularBTDF)

    override fun toString() = "Transparent(${super.toString()}, $reflectiveBRDF, $specularBTDF)"
}
