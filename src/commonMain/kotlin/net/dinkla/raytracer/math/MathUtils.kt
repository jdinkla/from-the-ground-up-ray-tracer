package net.dinkla.raytracer.math

object MathUtils {
    const val PI = 3.141592653589793
    const val INV_PI = 1.0 / PI
    const val PI_ON_180 = PI / 180
    const val K_HUGE_VALUE = 1.0E10
    const val K_EPSILON = 0.01

    fun clamp(
        x: Double,
        low: Double,
        high: Double,
    ): Double =
        if (x < low) {
            low
        } else if (x > high) {
            high
        } else {
            x
        }

    fun min(
        p: Point3D,
        q: Point3D,
        r: Point3D,
    ): Point3D {
        val x = min(p.x, q.x, r.x)
        val y = min(p.y, q.y, r.y)
        val z = min(p.z, q.z, r.z)
        return Point3D(x, y, z)
    }

    fun min(
        x: Double,
        y: Double,
        z: Double,
    ): Double =
        if (x < y) {
            if (x < z) x else z
        } else {
            if (y < z) y else z
        }

    fun maxMax(
        p: Point3D,
        q: Point3D,
        r: Point3D,
    ): Point3D {
        val x = max(p.x, q.x, r.x)
        val y = max(p.y, q.y, r.y)
        val z = max(p.z, q.z, r.z)
        return Point3D(x, y, z)
    }

    fun max(
        x: Double,
        y: Double,
        z: Double,
    ): Double =
        if (x > y) {
            if (x > z) x else z
        } else {
            if (y > z) y else z
        }

    fun isZero(x: Double): Boolean = x > -K_EPSILON && x < K_EPSILON
}
