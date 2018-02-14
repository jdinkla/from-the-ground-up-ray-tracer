package net.dinkla.raytracer.math

class Vector3D(x: Double, y: Double, z: Double) : Element3D(x, y, z) {

    // TODO ugly
    constructor(e: Element3D?) : this(e?.x ?: 0.0, e?.y ?: 0.0, e?.z ?: 0.0)  {}

    operator fun plus(v: Vector3D): Vector3D = Vector3D(x + v.x, y + v.y, z + v.z)

    operator fun minus(v: Vector3D): Vector3D = Vector3D(x - v.x, y - v.y, z - v.z)

    operator fun times(s: Double): Vector3D = Vector3D(s * x, s * y, s * z)

    infix fun dot(v: Vector3D): Double = x * v.x + y * v.y + z * v.z

    infix fun dot(v: Normal?): Double {
        if (null == v) return x * y * z
        else return x * v.x + y * v.y + z * v.z
    }

    infix fun cross(v: Vector3D): Vector3D {
        return Vector3D(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)
    }

    fun normalize(): Vector3D {
        val l = length()
        return Vector3D(x / l, y / l, z / l)
    }

    fun negate(): Vector3D = Vector3D(-x, -y, -z)

    fun volume(): Double = x * y * z

    companion object {
        val ZERO = Vector3D(0.0, 0.0, 0.0)
        val UP = Vector3D(0.0, 1.0, 0.0)
        val DOWN = Vector3D(0.0, -1.0, 0.0)
        val JITTER = Vector3D(0.0072, 1.0, 0.0034)
        val BACK = Vector3D(0.0, 0.0, -1.0)
    }

}

operator fun Double.times(v: Vector3D) = Vector3D(this * v.x, this * v.y, this * v.z)
