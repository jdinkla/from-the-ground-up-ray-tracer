package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.worlds.World

/**
 *
 * TODO: Da war shared state drin. Böse bei der Parallelisierung
 */
class AmbientOccluder : Ambient {

    //public Vector3D u, v, w;
    val minAmount: Color
    val sampler: Sampler
    val numSamples: Int

    constructor(sampler: Sampler, numSamples: Int) {
        this.minAmount = Color.WHITE
        this.sampler = sampler
        this.numSamples = numSamples
    }

    constructor(minAmount: Color, sampler: Sampler, numSamples: Int) {
        this.minAmount = minAmount
        this.sampler = sampler
        this.numSamples = numSamples
    }

    override fun L(world: World, sr: Shade): Color {
        val w = Vector3D(sr.normal)
        // jitter up vector in case normal is vertical
        val v = w.cross(Vector3D.JITTER).normalize()
        val u = v.cross(w)

        var numHits = 0
        for (i in 0 until numSamples) {
            val p = sampler.sampleHemisphere()
            val dir = u.times(p.x).plus(v.times(p.y)).plus(w.times(p.z))
            val shadowRay = Ray(sr.hitPoint, dir)
            if (inShadow(world, shadowRay, sr)) {
                numHits++
            }
        }
        val ratio = 1.0 - 1.0 * numHits / numSamples
        return color.mult(ls * ratio)
    }

    override fun getDirection(sr: Shade): Vector3D {
        val p = sampler.sampleHemisphere()
        val w = Vector3D(sr.normal)
        val v = w.cross(Vector3D.JITTER).normalize()
        val u = v.cross(w)
        return u.times(p.x).plus(v.times(p.y)).plus(w.times(p.z))
    }

    override fun inShadow(world: World, ray: Ray, sr: Shade): Boolean {
        return world.inShadow(ray, sr, java.lang.Double.MAX_VALUE)
    }
}
