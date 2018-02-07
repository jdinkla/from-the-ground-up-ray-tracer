package net.dinkla.raytracer.math

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:17:52
 * To change this template use File | Settings | File Templates.
 */
class Normal : Element3D {

    constructor(x: Double, y: Double, z: Double) : super(x, y, z) {}

    constructor(v: Vector3D) : super(v.normalize()) {}

    constructor(p0: Point3D, p1: Point3D, p2: Point3D) : super(p1.minus(p0).cross(p2.minus(p0)).normalize()) {}

    fun normalize(): Normal {
        val len = length()
        return Normal(x / len, y / len, z / len)
    }

    fun mult(s: Double): Vector3D {
        return Vector3D(s * x, s * y, s * z)
    }

    operator fun plus(normal: Normal): Vector3D {
        return Vector3D(x + normal.x, y + normal.y, z + normal.z)
    }

    fun dot(v: Vector3D): Double {
        return x * v.x + y * v.y + z * v.z
    }

    fun negate(): Normal {
        return Normal(-x, -y, -z)
    }

    companion object {
        val RIGHT = Normal(1.0, 0.0, 0.0)
        val LEFT = Normal(-1.0, 0.0, 0.0)
        val UP = Normal(0.0, 1.0, 0.0)
        val DOWN = Normal(0.0, -1.0, 0.0)
        val FRONT = Normal(0.0, 0.0, 1.0)
        val BACK = Normal(0.0, 0.0, -1.0)
        val ZERO = Normal(0.0, 0.0, 0.0)
    }
}
