package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 16:05:01
 * To change this template use File | Settings | File Templates.
 */
class Sphere : GeometricObject {

    var center: Point3D

    var radius: Double = 0.0

    protected var bbox: BBox? = null

    constructor(radius: Double) {
        this.center = Point3D.ORIGIN
        this.radius = radius
        bbox = null
    }

    constructor(center: Point3D, radius: Double) {
        this.center = center
        this.radius = radius
        bbox = null
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        var t: Double
        val temp = ray.o - center
        val a = ray.d.dot(ray.d)
        val b = temp.mult(2.0).dot(ray.d)
        val c = temp.dot(temp) - radius * radius
        val disc = b * b - 4.0 * a * c

        if (disc < 0) {
            return false
        } else {
            val e = Math.sqrt(disc)
            val denom = 2 * a
            t = (-b - e) / denom
            if (t > MathUtils.K_EPSILON) {
                sr.setT(t)
                sr.normal = Normal(ray.d.mult(t).plus(temp).mult(1.0 / radius))
                return true
            }
            t = (-b + e) / denom
            if (t > MathUtils.K_EPSILON) {
                sr.setT(t)
                sr.normal = Normal(ray.d.mult(t).plus(temp).mult(1.0 / radius))
                return true
            }
        }
        return false
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        var t: Double
        val temp = ray.o.minus(center)
        val a = ray.d.dot(ray.d)
        val b = temp.mult(2.0).dot(ray.d)
        val c = temp.dot(temp) - radius * radius
        val disc = b * b - 4.0 * a * c

        if (disc < 0) {
            return false
        } else {
            val e = Math.sqrt(disc)
            val denom = 2 * a
            t = (-b - e) / denom
            if (t > MathUtils.K_EPSILON) {
                tmin.setT(t)
                return true
            }
            t = (-b + e) / denom
            if (t > MathUtils.K_EPSILON) {
                tmin.setT(t)
                return true
            }
        }
        return false
    }

    override fun getBoundingBox(): BBox? {
        if (null == bbox) {
            bbox = BBox(center.minus(radius), center.plus(radius))
        }
        return bbox
        //return new BBox(center.minus(radius), center.plus(radius));
        //        return new BBox(center.minus(radius + MathUtils.K_EPSILON), center.plus(radius + MathUtils.K_EPSILON));
    }

    override fun toString(): String {
        return "Sphere(" + center.toString() + ", " + radius + ")"
    }

}
