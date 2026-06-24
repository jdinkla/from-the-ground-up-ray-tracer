package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.PerfectSpecular
import net.dinkla.raytracer.btdf.PerfectTransmitter
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.world.IWorld
import java.util.Objects
import kotlin.math.abs

class Transparent : Phong {
    private var reflectiveBRDF = PerfectSpecular()
    private var specularBTDF = PerfectTransmitter()

    @SuppressWarnings("LongParameterList")
    constructor(
        color: Color = Color.WHITE,
        ka: Double = 0.25,
        kd: Double = 0.75,
        exp: Double = 5.0,
        ks: Double = 0.25,
        cs: Color = Color.WHITE,
        kt: Double = 0.0,
        ior: Double = 0.0,
        kr: Double = 0.0,
        cr: Color = Color.WHITE,
    ) : super(color, ka, kd, exp, ks, cs) {
        this.kt = kt
        this.ior = ior
        this.kr = kr
        this.cr = cr
    }

    var kt: Double
        get() = specularBTDF.kt
        set(v) {
            specularBTDF.kt = v
        }

    var ior: Double
        get() = specularBTDF.ior
        set(v) {
            specularBTDF.ior = v
        }

    var kr: Double
        get() = reflectiveBRDF.kr
        set(v) {
            reflectiveBRDF.kr = v
        }

    var cr: Color
        get() = reflectiveBRDF.cr
        set(v) {
            reflectiveBRDF.cr = v
        }

    override fun shade(
        world: IWorld,
        sr: IShade,
    ): Color {
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

    /**
     * Path-tracing shade (Suffern ch. 28 §28.9, the global-illumination analogue of [shade]). A
     * transparent surface in the path tracer carries no direct (Phong) term — it spawns the
     * perfect-specular reflected ray and the perfect-transmitter transmitted ray one level deeper,
     * exactly the reflected/transmitted block of the Whitted [shade] minus `super.shade`. Under total
     * internal reflection ([PerfectTransmitter.isTir]) there is no transmitted ray and all energy
     * reflects, so only the reflected radiance is added (matching [shade]). This is what lets the path
     * tracer carry light refracted through the object onto another surface — a refractive caustic.
     * Returns black when the world has no tracer to recurse through.
     */
    override fun pathShade(
        world: IWorld,
        sr: IShade,
    ): Color {
        val tracer = world.tracer ?: return Color.BLACK
        var l = Color.BLACK
        val wo = sr.ray.direction.times(-1.0)
        val brdf = reflectiveBRDF.sampleF(sr, wo)
        // trace reflected ray
        val reflectedRay = Ray(sr.hitPoint, brdf.wi)
        val cr = tracer.trace(reflectedRay, sr.depth + 1)
        if (specularBTDF.isTir(sr)) {
            l += cr
        } else {
            // reflected
            val cfr = abs(sr.normal dot brdf.wi)
            l += (brdf.color * cr) * cfr

            // trace transmitted ray
            val btdf = specularBTDF.sampleF(sr, wo)
            val transmittedRay = Ray(sr.hitPoint, btdf.wt)
            val ct = tracer.trace(transmittedRay, sr.depth + 1)
            val cft = abs(sr.normal dot btdf.wt)
            l += (btdf.color * ct) * cft
        }
        return l
    }

    override fun equals(other: Any?): Boolean =
        this.equals<Transparent>(other) { a, b ->
            a.diffuseBRDF == b.diffuseBRDF &&
                a.ambientBRDF == b.ambientBRDF &&
                a.specularBTDF == b.specularBTDF &&
                a.reflectiveBRDF == b.reflectiveBRDF &&
                a.specularBRDF == b.specularBRDF
        }

    override fun hashCode(): Int =
        Objects.hash(super.diffuseBRDF, super.ambientBRDF, specularBRDF, reflectiveBRDF, specularBTDF)

    override fun toString() = "Transparent(${super.toString()}, $reflectiveBRDF, $specularBTDF)"
}
