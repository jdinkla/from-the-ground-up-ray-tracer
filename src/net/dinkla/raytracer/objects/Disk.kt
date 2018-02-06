package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.lights.ILightSource
import net.dinkla.raytracer.math.*

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 19:06:27
 * To change this template use File | Settings | File Templates.
 */
open class Disk(var center: Point3D, var radius: Float, var normal: Normal) : GeometricObject() {

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

    override fun getBoundingBox(): BBox {
        // TODO: more exact bounding box of a disk
        val p = center.minus(radius)
        val q = center.plus(radius)
        return BBox(p, q)
    }

}
