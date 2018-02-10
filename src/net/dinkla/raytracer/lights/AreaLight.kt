package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.materials.Material
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.worlds.World

import java.util.ArrayList


class AreaLight : Light(), ILightSource {

    var `object`: ILightSource? = null

    // Emissive Material TODO: Warum nicht Emissive?
    var material: Material? = null

    var numSamples: Int = 0

    inner class Sample {
        var samplePoint: Point3D? = null
        var lightNormal: Normal? = null
        var wi: Vector3D? = null

        val nDotD: Double
            get() = lightNormal!!.negate().dot(wi!!)
    }

    init {
        numSamples = 4
    }

    fun L(world: World, sr: Shade, sample: Sample): Color {
        return if (sample.nDotD > 0) {
            sr.material.getLe(sr)
        } else {
            Color.BLACK
        }
    }

    fun inShadow(world: World, ray: Ray, sr: Shade, sample: Sample): Boolean {
        val d = sample.samplePoint!!.minus(ray.o).dot(ray.d)
        return world.inShadow(ray, sr, d)
    }

    fun G(sr: Shade, sample: Sample): Double {
        val nDotD = sample.nDotD
        val d2 = sample.samplePoint!!.distanceSquared(sr.hitPoint)
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
