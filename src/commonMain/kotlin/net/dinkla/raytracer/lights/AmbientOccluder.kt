package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.world.IWorld

class AmbientOccluder(
    val minAmount: Color,
    val sampler: Sampler,
    val numSamples: Int,
    override val shadows: Boolean = true
) : Ambient() {

    override fun L(world: IWorld, sr: IShade): Color {
        val w = sr.normal.toVector3D()
        val v = w cross (Vector3D.JITTER).normalize() // jitter up vector in case normal is vertical
        val u = v cross w

        var numHits = 0.0
        for (i in 0 until numSamples) {
            val p = sampler.sampleHemisphere()
            val dir = u * p.x + v * p.y + w * p.z
            val shadowRay = Ray(sr.hitPoint, dir)
            if (inShadow(world, shadowRay, sr)) {
                numHits++
            }
        }
        val ratio = 1.0 - numHits / numSamples
        return color * (ls * ratio)
    }

    override fun getDirection(sr: IShade): Vector3D {
        val p = sampler.sampleHemisphere()
        val w = sr.normal.toVector3D()
        val v = w cross (Vector3D.JITTER).normalize()
        val u = v cross w
        return Basis(u, v, w) * p
    }

    override fun inShadow(world: IWorld, ray: Ray, sr: IShade): Boolean =
        world.inShadow(ray, sr, Double.MAX_VALUE)
}
