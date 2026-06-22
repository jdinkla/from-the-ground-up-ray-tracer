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
class AreaLight(
    override val shadows: Boolean = true,
) : Light,
    ILightSource {
    var source: ILightSource? = null
    var material: IMaterial? = null
    var numSamples: Int = 4

    inner class Sample {
        var samplePoint: Point3D? = null
        var lightNormal: Normal? = null
        var wi: Vector3D? = null

        val nDotD: Double
            get() {
                val normal = requireNotNull(lightNormal) { "Sample.lightNormal not set; call getSample first" }
                val direction = requireNotNull(wi) { "Sample.wi not set; call getSample first" }
                return (-normal) dot direction
            }
    }

    fun l(
        world: IWorld,
        sr: IShade,
        sample: Sample,
    ): Color =
        if (sample.nDotD > 0) {
            sr.material?.getLe(sr) ?: world.backgroundColor
        } else {
            Color.BLACK
        }

    fun inShadow(
        world: IWorld,
        ray: Ray,
        sr: IShade,
        sample: Sample,
    ): Boolean {
        val samplePoint = requireNotNull(sample.samplePoint) { "Sample.samplePoint not set; call getSample first" }
        val d = samplePoint.minus(ray.origin).dot(ray.direction)
        return world.inShadow(ray, sr, d)
    }

    fun G(
        sr: IShade,
        sample: Sample,
    ): Double {
        val nDotD = sample.nDotD
        val samplePoint = requireNotNull(sample.samplePoint) { "Sample.samplePoint not set; call getSample first" }
        val d2 = samplePoint.sqrDistance(sr.hitPoint)
        return nDotD / d2
    }

    override fun pdf(sr: IShade): Double = requiredSource().pdf(sr)

    private fun requiredSource(): ILightSource =
        requireNotNull(source) { "AreaLight.source not set; assign a light source before rendering" }

    private fun getSample(sr: IShade): Sample {
        val src = requiredSource()
        val sample = Sample()
        val samplePoint = src.sample()
        sample.samplePoint = samplePoint
        sample.lightNormal = src.getNormal(samplePoint)
        sample.wi = samplePoint.minus(sr.hitPoint).normalize()
        return sample
    }

    fun getSamples(sr: IShade): List<Sample> {
        val result = ArrayList<Sample>()
        for (i in 0 until numSamples) {
            result.add(getSample(sr))
        }
        return result
    }

    override fun sample(): Point3D = throw UnsupportedOperationException(NEEDS_AREA_LIGHTING)

    override fun getNormal(p: Point3D): Normal = throw UnsupportedOperationException(NEEDS_AREA_LIGHTING)

    override fun l(
        world: IWorld,
        sr: IShade,
    ): Color = throw UnsupportedOperationException(NEEDS_AREA_LIGHTING)

    override fun getDirection(sr: IShade): Vector3D = throw UnsupportedOperationException(NEEDS_AREA_LIGHTING)

    override fun inShadow(
        world: IWorld,
        ray: Ray,
        sr: IShade,
    ): Boolean = throw UnsupportedOperationException(NEEDS_AREA_LIGHTING)

    override fun getLightMaterial(): IMaterial =
        requireNotNull(material) { "AreaLight.material not set; assign a material before rendering" }

    private companion object {
        const val NEEDS_AREA_LIGHTING = "AreaLight needs AreaLighting Tracer"
    }
}
