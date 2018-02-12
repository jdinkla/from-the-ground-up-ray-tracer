package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 16:06:34
 * To change this template use File | Settings | File Templates.
 */
class Plane : GeometricObject {

    var point: Point3D
    var normal: Normal

    constructor() {
        this.point = Point3D.ORIGIN
        this.normal = Normal.UP
        boundingBox = BBox(Point3D.MIN, Point3D.MAX)
    }

    constructor(point: Point3D, normal: Normal) {
        this.point = point
        this.normal = normal
        boundingBox = BBox(Point3D.MIN, Point3D.MAX)
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        // (point - ray.origin) * normal / (ray.direction * normal)
        val v = point - ray.origin
        val nom = v.dot(normal)
        val denom = ray.direction.dot(normal)
        val t = nom / denom
        if (t > MathUtils.K_EPSILON) {
            sr.t = t
            sr.normal = this.normal
            return true
        } else {
            return false
        }
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        val v = point - ray.origin
        val nom = v.dot(normal)
        val denom = ray.direction.dot(normal)
        val t = nom / denom
        if (t > MathUtils.K_EPSILON) {
            tmin.t = t
            return true
        } else {
            return false
        }
    }

    override fun toString(): String {
        return "Plane: " + super.toString() + " " + point.toString() + " " + normal.toString()
    }
}
