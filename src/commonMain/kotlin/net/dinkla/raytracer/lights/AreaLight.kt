package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.world.IWorld

@Suppress("TooManyFunctions")
class AreaLight(override val shadows: Boolean = true) : Light, ILightSource {

    var source: ILightSource? = null

    // Emissive Material TODO: Warum nicht Emissive?
    var material: IMaterial? = null

    var numSamples: Int = 4

    inner class Sample {
        var samplePoint: Point3D? = null
        var lightNormal: Normal? = null
        var wi: Vector3D? = null

        val nDotD: Double
            get() = (-lightNormal!!) dot (wi!!)
    }

    fun L(world: IWorld, sr: IShade, sample: Sample): Color {
        return if (sample.nDotD > 0) {
            sr.material?.getLe(sr) ?: world.backgroundColor
        } else {
            Color.BLACK
        }
    }

    fun inShadow(world: IWorld, ray: Ray, sr: IShade, sample: Sample): Boolean {
        val d = sample.samplePoint!!.minus(ray.origin).dot(ray.direction)
        return world.inShadow(ray, sr, d)
    }

    fun G(sr: IShade, sample: Sample): Double {
        val nDotD = sample.nDotD
        val d2 = sample.samplePoint!!.sqrDistance(sr.hitPoint)
        return nDotD / d2
    }

    override fun pdf(sr: IShade): Double {
        return source!!.pdf(sr)
    }

    private fun getSample(sr: IShade): Sample {
        val sample = Sample()
        sample.samplePoint = source!!.sample()
        sample.lightNormal = source!!.getNormal(sample.samplePoint!!)
        sample.wi = sample.samplePoint!!.minus(sr.hitPoint).normalize()
        return sample
    }

    fun getSamples(sr: IShade): List<Sample> {
        val result = ArrayList<Sample>()
        for (i in 0 until numSamples) {
            result.add(getSample(sr))
        }
        return result
    }

    override fun sample(): Point3D {
        throw RuntimeException("AreaLight needs AreaLighting Tracer")
    }

    override fun getNormal(p: Point3D): Normal {
        throw RuntimeException("AreaLight needs AreaLighting Tracer")
    }

    override fun L(world: IWorld, sr: IShade): Color {
        throw RuntimeException("AreaLight needs AreaLighting Tracer")
    }

    override fun getDirection(sr: IShade): Vector3D {
        throw RuntimeException("AreaLight needs AreaLighting Tracer")
    }

    override fun inShadow(world: IWorld, ray: Ray, sr: IShade): Boolean {
        throw RuntimeException("AreaLight needs AreaLighting Tracer")
    }

    override fun getLightMaterial(): IMaterial = material!!
}
