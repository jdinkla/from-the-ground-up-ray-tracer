package net.dinkla.raytracer.math

class Vector3D : Element3D {

    constructor(x: Float, y: Float, z: Float) : super(x, y, z) {}

    constructor(x: Double, y: Double, z: Double) : super(x, y, z) {}

    constructor(e: Element3D) : super(e) {}

    operator fun plus(v: Vector3D): Vector3D {
        return Vector3D(x + v.x, y + v.y, z + v.z)
    }

    operator fun minus(v: Vector3D): Vector3D {
        return Vector3D(x - v.x, y - v.y, z - v.z)
    }

    fun mult(s: Float): Vector3D {
        return Vector3D(s * x, s * y, s * z)
    }

    operator fun times(s: Float): Vector3D {
        return Vector3D(s * x, s * y, s * z)
    }

    fun dot(v: Vector3D): Float {
        return x * v.x + y * v.y + z * v.z
    }

    fun dot(v: Normal): Float {
        return x * v.x + y * v.y + z * v.z
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

        val ZERO = Vector3D(0f, 0f, 0f)
        val UP = Vector3D(0f, 1f, 0f)
        val DOWN = Vector3D(0f, -1f, 0f)
        val JITTER = Vector3D(0.0072f, 1.0f, 0.0034f)
        val BACK = Vector3D(0f, 0f, -1f)
    }

}
