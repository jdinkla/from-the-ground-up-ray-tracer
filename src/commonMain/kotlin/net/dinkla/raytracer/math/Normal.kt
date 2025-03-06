package net.dinkla.raytracer.math

import kotlin.math.sqrt

data class Normal(
    val x: Double,
    val y: Double,
    val z: Double,
) {
    operator fun plus(normal: Normal) = Vector3D(x + normal.x, y + normal.y, z + normal.z)

    operator fun times(s: Double) = Vector3D(s * x, s * y, s * z)

    operator fun unaryMinus() = Normal(-x, -y, -z)

    infix fun dot(v: Vector3D): Double = x * v.x + y * v.y + z * v.z

    fun normalize(): Normal {
        val len = length()
        return Normal(x / len, y / len, z / len)
    }

    private fun sqrLength(): Double = x * x + y * y + z * z

    fun length(): Double = sqrt(sqrLength())

    fun toVector3D() = Vector3D(x, y, z)

    companion object {
        fun create(v: Vector3D): Normal {
            val n = v.normalize()
            return Normal(n.x, n.y, n.z)
        }

        fun create(
            p0: Point3D,
            p1: Point3D,
            p2: Point3D,
        ): Normal {
            val n = ((p1 - p0) cross (p2 - p0)).normalize()
            return Normal(n.x, n.y, n.z)
        }

        val RIGHT = Normal(1.0, 0.0, 0.0)
        val LEFT = Normal(-1.0, 0.0, 0.0)
        val UP = Normal(0.0, 1.0, 0.0)
        val DOWN = Normal(0.0, -1.0, 0.0)
        val ONE = Normal(1.0, 1.0, 1.0)
        val FORWARD = Normal(0.0, 0.0, 1.0)
        val BACKWARD = Normal(0.0, 0.0, -1.0)
        val ZERO = Normal(0.0, 0.0, 0.0)
    }
}
