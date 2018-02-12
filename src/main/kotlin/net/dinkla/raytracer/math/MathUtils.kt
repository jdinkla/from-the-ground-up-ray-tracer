package net.dinkla.raytracer.math

object MathUtils {

    val INV_PI = 1.0 / Math.PI

    val PI_ON_180 = Math.PI / 180

    val K_HUGEVALUE = 1.0E10

    val K_EPSILON = 0.01

    fun clamp(x: Double, low: Double, high: Double): Double {
        return if (x < low) low else if (x > high) high else x
    }

    fun minMin(p: Point3D, q: Point3D, r: Point3D): Point3D {
        val x = Math.min(Math.min(p.x, q.x), r.x)
        val y = Math.min(Math.min(p.y, q.y), r.y)
        val z = Math.min(Math.min(p.z, q.z), r.z)
        return Point3D(x, y, z)
    }

    fun maxMax(p: Point3D, q: Point3D, r: Point3D): Point3D {
        val x = Math.max(Math.max(p.x, q.x), r.x)
        val y = Math.max(Math.max(p.y, q.y), r.y)
        val z = Math.max(Math.max(p.z, q.z), r.z)
        return Point3D(x, y, z)
    }

    fun isZero(r: Double): Boolean {
        return r > -K_EPSILON && r < K_EPSILON
    }

}
