package net.dinkla.raytracer.objects.arealights

import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.lights.ILightSource
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.MathUtils.PI
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector2D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Disk
import net.dinkla.raytracer.samplers.Sampler

class DiskLight(
    val sampler: Sampler,
    center: Point3D,
    radius: Double,
    normal: Normal
) : Disk(center, radius, normal), ILightSource {

    val pdf = 1.0 / (PI * radius * radius)

    override fun pdf(sr: Shade): Double = pdf

    override fun sample(): Point3D {
        val sp = sampler.sampleUnitDisk()
        val v = Vector2D(sp.x * radius, sp.y * radius)
        return center + Vector3D(v.x, v.y, 0.0)
    }

    override fun getLightMaterial(): IMaterial = material!!

}
