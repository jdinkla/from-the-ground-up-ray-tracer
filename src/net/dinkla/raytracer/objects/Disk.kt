package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.lights.ILightSource
import net.dinkla.raytracer.math.*

open class Disk(var center: Point3D, var radius: Double, var normal: Normal) : GeometricObject() {

    init {
        // TODO: more exact bounding box of a disk
        val p = center.minus(radius)
        val q = center.plus(radius)
        boundingBox = BBox(p, q)
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        val nom = center.minus(ray.o).dot(normal)
        val denom = ray.d.dot(normal)
        val t = nom / denom
        if (t <= MathUtils.K_EPSILON) {
            return false
        }
        val p = ray.linear(t)
        if (center.distanceSquared(p) < radius * radius) {
            sr.setT(t)
            sr.normal = normal
            //            sr.localHitPoint = p;
            return true
        } else {
            return false
        }
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        val nom = center.minus(ray.o).dot(normal)
        val denom = ray.d.dot(normal)
        val t = nom / denom
        if (t <= MathUtils.K_EPSILON) {
            return false
        }
        val p = ray.linear(t)
        if (center.distanceSquared(p) < radius * radius) {
            tmin.setT(t)
            return true
        } else {
            return false
        }
    }

    fun getNormal(p: Point3D): Normal {
        return normal
    }
}
