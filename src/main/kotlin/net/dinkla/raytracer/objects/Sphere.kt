package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*

class Sphere : GeometricObject {

    var center: Point3D

    var radius: Double = 0.0

    constructor(radius: Double) {
        this.center = Point3D.ORIGIN
        this.radius = radius
        boundingBox = BBox(center.minus(radius), center.plus(radius))
    }

    constructor(center: Point3D, radius: Double) {
        this.center = center
        this.radius = radius
        boundingBox = BBox(center.minus(radius), center.plus(radius))
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        var t: Double
        val temp = ray.origin - center
        val a = ray.direction.dot(ray.direction)
        val b = temp.times(2.0).dot(ray.direction)
        val c = temp.dot(temp) - radius * radius
        val disc = b * b - 4.0 * a * c

        if (disc < 0) {
            return false
        } else {
            val e = Math.sqrt(disc)
            val denom = 2 * a
            t = (-b - e) / denom
            if (t > MathUtils.K_EPSILON) {
                sr.t = t
                sr.normal = Normal(ray.direction.times(t).plus(temp).times(1.0 / radius))
                return true
            }
            t = (-b + e) / denom
            if (t > MathUtils.K_EPSILON) {
                sr.t = t
                sr.normal = Normal(ray.direction.times(t).plus(temp).times(1.0 / radius))
                return true
            }
        }
        return false
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        var t: Double
        val temp = ray.origin.minus(center)
        val a = ray.direction.dot(ray.direction)
        val b = temp.times(2.0).dot(ray.direction)
        val c = temp.dot(temp) - radius * radius
        val disc = b * b - 4.0 * a * c

        if (disc < 0) {
            return false
        } else {
            val e = Math.sqrt(disc)
            val denom = 2 * a
            t = (-b - e) / denom
            if (t > MathUtils.K_EPSILON) {
                tmin.t = t
                return true
            }
            t = (-b + e) / denom
            if (t > MathUtils.K_EPSILON) {
                tmin.t = t
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        return "Sphere(" + center.toString() + ", " + radius + ")"
    }

}
