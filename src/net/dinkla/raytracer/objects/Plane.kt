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
        // (point - ray.o) * normal / (ray.d * normal)
        val v = point - ray.o
        val nom = v.dot(normal)
        val denom = ray.d.dot(normal)
        val t = nom / denom
        if (t > MathUtils.K_EPSILON) {
            sr.setT(t)
            sr.normal = this.normal
            return true
        } else {
            return false
        }
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        val v = point - ray.o
        val nom = v.dot(normal)
        val denom = ray.d.dot(normal)
        val t = nom / denom
        if (t > MathUtils.K_EPSILON) {
            tmin.setT(t)
            return true
        } else {
            return false
        }
    }

    override fun toString(): String {
        return "Plane: " + super.toString() + " " + point.toString() + " " + normal.toString()
    }
}
