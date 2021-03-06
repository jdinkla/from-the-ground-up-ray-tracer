package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.interfaces.hash

class Plane(val point: Point3D = Point3D.ORIGIN, val normal: Normal = Normal.UP) : GeometricObject() {

    init {
        boundingBox = BBox(Point3D.MIN, Point3D.MAX)
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        // (point - ray.origin) * normal / (ray.direction * normal)
        val v = point - ray.origin
        val nom = v.dot(normal)
        val denom = ray.direction.dot(normal)
        val t = nom / denom
        return if (t > MathUtils.K_EPSILON) {
            sr.t = t
            sr.normal = this.normal
            true
        } else {
            false
        }
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        val v = point - ray.origin
        val nom = v.dot(normal)
        val denom = ray.direction.dot(normal)
        val t = nom / denom
        return if (t > MathUtils.K_EPSILON) {
            tmin.t = t
            true
        } else {
            false
        }
    }

    override fun equals(other: Any?): Boolean = this.equals<Plane>(other) { a, b ->
        a.point == b.point && a.normal == b.normal
    }

    override fun hashCode(): Int = this.hash(point, normal)

    override fun toString(): String = "Plane($point, $normal)"
}
