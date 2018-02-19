package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*

open class Disk(var center: Point3D, var radius: Double, var normal: Normal) : GeometricObject() {

    init {
        boundingBox = BBox(center - radius, center + radius)
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        val nom = (center - ray.origin) dot normal
        val denom = ray.direction dot normal
        val t = nom / denom
        if (t <= MathUtils.K_EPSILON) {
            return false
        }
        val p = ray.linear(t)
        if (center.sqrDistance(p) < radius * radius) {
            sr.t = t
            sr.normal = normal
            //            sr.localHitPoint = p;
            return true
        } else {
            return false
        }
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        val nom = (center - ray.origin) dot normal
        val denom = ray.direction dot normal
        val t = nom / denom
        if (t <= MathUtils.K_EPSILON) {
            return false
        }
        val p = ray.linear(t)
        if (center.sqrDistance(p) < radius * radius) {
            tmin.t = t
            return true
        } else {
            return false
        }
    }

    // TODO why with p?
    fun getNormal(p: Point3D): Normal = normal
}
