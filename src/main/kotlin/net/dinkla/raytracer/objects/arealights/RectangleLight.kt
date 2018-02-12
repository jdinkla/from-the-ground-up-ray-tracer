package net.dinkla.raytracer.objects.arealights

import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.lights.ILightSource
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Rectangle
import net.dinkla.raytracer.samplers.Sampler

class RectangleLight(p0: Point3D, a: Vector3D, b: Vector3D) : Rectangle(p0, a, b), ILightSource {

    var sampler: Sampler? = null

    protected var pdf: Double = 0.toDouble()

    init {
        pdf = 1.0 / (a.length() * b.length())
    }

    override fun pdf(sr: Shade): Double {
        return pdf
    }

    override fun sample(): Point3D {
        val sp = sampler!!.sampleUnitSquare()
        return p0.plus(a.times(sp.x)).plus(b.times(sp.y))
    }

}


