package net.dinkla.raytracer.objects.arealights

import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.lights.ILightSource
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.objects.Disk
import net.dinkla.raytracer.samplers.Sampler

class DiskLight(center: Point3D, radius: Double, normal: Normal) : Disk(center, radius, normal), ILightSource {

    var sampler: Sampler? = null

    override fun pdf(sr: Shade): Double {
        return 0.0  //To change body of implemented methods use File | Settings | File Templates.
    }

    // TODO: sample auf ner disk
    override fun sample(): Point3D {
        val sp = sampler!!.sampleUnitDisk()
        assert(null != sampler)
        val v = Vector2D(sp.x * radius, sp.y * radius)
        return center.plus(Vector3D(v.x, v.y, 0.0))
    }
}
