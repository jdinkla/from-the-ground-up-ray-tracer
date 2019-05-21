package net.dinkla.raytracer.math

class Vector3D(x: Double, y: Double, z: Double) : Element3D(x, y, z) {

    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())

    constructor(e: Element3D) : this(e.x, e.y, e.z)

    operator fun plus(v: Vector3D): Vector3D = Vector3D(x + v.x, y + v.y, z + v.z)

    operator fun minus(v: Vector3D): Vector3D = Vector3D(x - v.x, y - v.y, z - v.z)

    operator fun times(s: Double): Vector3D = Vector3D(s * x, s * y, s * z)

    infix fun dot(v: Vector3D): Double = x * v.x + y * v.y + z * v.z

    infix fun dot(v: Normal?): Double {
        return if (null == v) x * y * z
        else x * v.x + y * v.y + z * v.z
    }

    infix fun cross(v: Vector3D): Vector3D = Vector3D(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)

    fun normalize(): Vector3D {
        val l = length()
        return Vector3D(x / l, y / l, z / l)
    }

    operator fun unaryMinus() = Vector3D(-x, -y, -z)

    fun volume(): Double = x * y * z

    companion object {
        val ZERO = Vector3D(0, 0, 0)
        val RIGHT = Vector3D(1, 0, 0)
        val UP = Vector3D(0, 1, 0)
        val DOWN = Vector3D(0, -1, 0)
        val FORWARD = Vector3D(0, 0, 1)
        val BACK = Vector3D(0, 0, -1)
        val JITTER = Vector3D(0.0072, 1.0, 0.0034)
    }

}

operator fun Double.times(v: Vector3D) = Vector3D(this * v.x, this * v.y, this * v.z)
