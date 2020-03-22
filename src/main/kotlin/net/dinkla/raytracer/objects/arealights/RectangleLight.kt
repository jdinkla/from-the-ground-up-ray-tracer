package net.dinkla.raytracer.objects.arealights

import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.lights.ILightSource
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Rectangle
import net.dinkla.raytracer.samplers.Sampler

class RectangleLight(val sampler: Sampler,
                     p0: Point3D,
                     a: Vector3D,
                     b: Vector3D,
                     normal: Normal) : Rectangle(p0, a, b, normal), ILightSource {

    constructor(sampler: Sampler, p0: Point3D, a: Vector3D, b: Vector3D)
            : this(sampler, p0, a, b, Normal((a cross b).normalize()))

    private var pdf: Double = 1.0 / (a.length() * b.length())

    override fun pdf(sr: Shade): Double = pdf

    override fun sample(): Point3D {
        val sp = sampler.sampleUnitSquare()
        return (p0 + a * sp.x) + b * sp.y
    }

    override fun getLightMaterial(): IMaterial = material!!

}


