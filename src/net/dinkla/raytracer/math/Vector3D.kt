package net.dinkla.raytracer.math

class Vector3D : Element3D {

    constructor(x: Double, y: Double, z: Double) : super(x, y, z) {}

    constructor(e: Element3D?) : super(if (null == e) ZERO else e) {}

    operator fun plus(v: Vector3D): Vector3D {
        return Vector3D(x + v.x, y + v.y, z + v.z)
    }

    operator fun minus(v: Vector3D): Vector3D {
        return Vector3D(x - v.x, y - v.y, z - v.z)
    }

    fun mult(s: Double): Vector3D {
        return Vector3D(s * x, s * y, s * z)
    }

    operator fun times(s: Double): Vector3D {
        return Vector3D(s * x, s * y, s * z)
    }

    fun dot(v: Vector3D): Double {
        return x * v.x + y * v.y + z * v.z
    }

    fun dot(v: Normal?): Double {
        if (null == v) return x * y * z
        else return x * v.x + y * v.y + z * v.z
    }

    fun cross(v: Vector3D): Vector3D {
        return Vector3D(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)
    }

    fun normalize(): Vector3D {
        val l = length()
        return Vector3D(x / l, y / l, z / l)
    }

    fun negate(): Vector3D {
        return Vector3D(-x, -y, -z)
    }

    companion object {
        val ZERO = Vector3D(0.0, 0.0, 0.0)
        val UP = Vector3D(0.0, 1.0, 0.0)
        val DOWN = Vector3D(0.0, -1.0, 0.0)
        val JITTER = Vector3D(0.0072, 1.0, 0.0034)
        val BACK = Vector3D(0.0, 0.0, -1.0)
    }

}
