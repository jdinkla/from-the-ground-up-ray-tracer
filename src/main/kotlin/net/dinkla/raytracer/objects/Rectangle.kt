package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.interfaces.hash

open class Rectangle : GeometricObject {

    val p0: Point3D
    val a: Vector3D
    val b: Vector3D
    val normal: Normal

    constructor(p0: Point3D, a: Vector3D, b: Vector3D) {
        this.p0 = p0
        this.a = a
        this.b = b
        val v = a cross b
        normal = Normal(v.normalize())
        boundingBox = calcBoundingBox()
    }

    constructor(p0: Point3D, a: Vector3D, b: Vector3D, inverted: Boolean) {
        this.p0 = p0
        this.a = a
        this.b = b
        val v: Vector3D
        if (inverted) {
            v = b.cross(a)
        } else {
            v = a.cross(b)
        }
        normal = Normal(v.normalize())
        boundingBox = calcBoundingBox()
    }

    constructor(p0: Point3D, a: Vector3D, b: Vector3D, normal: Normal) {
        this.p0 = p0
        this.a = a
        this.b = b
        this.normal = normal
        boundingBox = calcBoundingBox()
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        val nom = p0.minus(ray.origin).dot(normal)
        val denom = ray.direction.dot(normal)
        val t = nom / denom

        if (t <= MathUtils.K_EPSILON) {
            return false
        }

        val p = ray.linear(t)
        val d = p.minus(p0)

        val ddota = d.dot(a)
        if (ddota < 0 || ddota > a.sqrLength()) {
            return false
        }

        val ddotb = d.dot(b)
        if (ddotb < 0 || ddotb > b.sqrLength()) {
            return false
        }

        sr.t = t
        sr.normal = normal

        return true
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        val nom = p0.minus(ray.origin).dot(normal)
        val denom = ray.direction.dot(normal)
        val t = nom / denom

        if (t <= MathUtils.K_EPSILON) {
            return false
        }

        val p = ray.linear(t)
        val d = p.minus(p0)

        val ddota = d.dot(a)
        if (ddota < 0 || ddota > a.sqrLength()) {
            return false
        }

        val ddotb = d.dot(b)
        if (ddotb < 0 || ddotb > b.sqrLength()) {
            return false
        }

        tmin.t = t
        return true
    }

    fun getNormal(p: Point3D): Normal {
        return normal
    }

    fun calcBoundingBox(): BBox {
        val v0 = p0
        val v1 = p0.plus(a).plus(b)

        var x0 = java.lang.Double.POSITIVE_INFINITY
        var x1 = java.lang.Double.NEGATIVE_INFINITY
        if (v0.x < x0) {
            x0 = v0.x
        }
        if (v1.x < x0) {
            x0 = v1.x
        }
        if (v0.x > x1) {
            x1 = v0.x
        }
        if (v1.x > x1) {
            x1 = v1.x
        }
        var y0 = java.lang.Double.POSITIVE_INFINITY
        var y1 = java.lang.Double.NEGATIVE_INFINITY
        if (v0.y < y0) {
            y0 = v0.y
        }
        if (v1.y < y0) {
            y0 = v1.y
        }
        if (v0.y > y1) {
            y1 = v0.y
        }
        if (v1.y > y1) {
            y1 = v1.y
        }
        var z0 = java.lang.Double.POSITIVE_INFINITY
        var z1 = java.lang.Double.NEGATIVE_INFINITY
        if (v0.z < z0) {
            z0 = v0.z
        }
        if (v1.z < z0) {
            z0 = v1.z
        }
        if (v0.z > z1) {
            z1 = v0.z
        }
        if (v1.z > z1) {
            z1 = v1.z
        }
        return BBox(Point3D(x0, y0, z0), Point3D(x1, y1, z1))
    }

    override fun equals(other: Any?): Boolean = this.equals<Rectangle>(other) { a, b ->
        a.p0 == b.p0 && a.a == b.a && a.b == b.b && a.normal == b.normal
    }

    override fun hashCode(): Int = this.hash(p0, a, b, normal)

    override fun toString(): String = "Rectangle($p0, $a, $b, $normal)"
}
