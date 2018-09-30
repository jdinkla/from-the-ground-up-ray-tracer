package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.worlds.World
import java.util.*


class AreaLight : Light(), ILightSource {

    var `object`: ILightSource? = null

    // Emissive Material TODO: Warum nicht Emissive?
    var material: IMaterial? = null

    var numSamples: Int = 0

    inner class Sample {
        var samplePoint: Point3D? = null
        var lightNormal: Normal? = null
        var wi: Vector3D? = null

        val nDotD: Double
            get() = (-lightNormal!!) dot (wi!!)
    }

    init {
        numSamples = 4
    }

    fun L(world: World, sr: Shade, sample: Sample): Color {
        return if (sample.nDotD > 0) {
            sr.material?.getLe(sr) ?: world.backgroundColor
        } else {
            Color.BLACK
        }
    }

    fun inShadow(world: World, ray: Ray, sr: Shade, sample: Sample): Boolean {
        val d = sample.samplePoint!!.minus(ray.origin).dot(ray.direction)
        return world.inShadow(ray, sr, d)
    }

    fun G(sr: Shade, sample: Sample): Double {
        val nDotD = sample.nDotD
        val d2 = sample.samplePoint!!.sqrDistance(sr.hitPoint)
        return nDotD / d2
    }

    override fun pdf(sr: Shade): Double {
        return `object`!!.pdf(sr)
    }

    fun getSample(sr: Shade): Sample {
        val sample = Sample()
        sample.samplePoint = `object`!!.sample()
        sample.lightNormal = `object`!!.getNormal(sample.samplePoint!!)
        sample.wi = sample.samplePoint!!.minus(sr.hitPoint).normalize()
        return sample
    }

    fun getSamples(sr: Shade): List<Sample> {
        val result = ArrayList<Sample>()
        for (i in 0 until numSamples) {
            result.add(getSample(sr))
        }
        return result
    }

    override fun sample(): Point3D {
        throw RuntimeException("NLU")
    }

    override fun getNormal(p: Point3D): Normal {
        throw RuntimeException("NLU")
    }

    override fun L(world: World, sr: Shade): Color {
        throw RuntimeException("NLU")
    }

    override fun getDirection(sr: Shade): Vector3D {
        throw RuntimeException("NLU")
    }

    override fun inShadow(world: World, ray: Ray, sr: Shade): Boolean {
        throw RuntimeException("NLU")
    }

}
