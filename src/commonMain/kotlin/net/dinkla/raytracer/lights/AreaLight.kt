package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.world.IWorld

/**
 * A light that emits over the area of a geometric [source] (a [DiskLight]/[RectangleLight]), sampled
 * with [numSamples] shadow rays by the `AreaLighting` tracer to produce soft shadows. It is a [Light]
 * but deliberately **not** a [DirectLight]: it has no single incoming direction, so the per-light
 * loop in the direct-lighting materials skips it and `AreaLighting` drives its sample-based
 * [l]/[inShadow]/[G]/[pdf] instead. It also is not an [ILightSource] itself — it *holds* one as its
 * [source] (TASK-63 removed the former `Light`/`ILightSource` stubs that threw
 * UnsupportedOperationException).
 */
@Suppress("TooManyFunctions")
class AreaLight(
    override val shadows: Boolean = true,
) : Light {
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

    /**
     * The radiance arriving at the shading point from this area-light sample (Suffern ch. 18,
     * `AreaLight::L`). When the sample faces the surface (`nDotD > 0`) it is the **light emitter's
     * own** emitted radiance — `getLightMaterial().getLe(sr)`, i.e. the panel's `ce * ls` — not the
     * receiving surface's `getLe`. Reading the receiver's `getLe` (the historical bug fixed in
     * TASK-54) made AREA lights far too dim, because a diffuse/specular receiver returns only
     * `cd*kd` / `cs*ks`, a fraction of the emitter's intensity. This mirrors [Matte.globalDirect]'s
     * `light.getLightMaterial().getLe` read.
     */
    fun l(
        sr: IShade,
        sample: Sample,
    ): Color =
        if (sample.nDotD > 0) {
            getLightMaterial().getLe(sr)
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

    fun pdf(sr: IShade): Double = requiredSource().pdf(sr)

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

    fun getLightMaterial(): IMaterial =
        requireNotNull(material) { "AreaLight.material not set; assign a material before rendering" }
}
