package net.dinkla.raytracer.math

import java.lang.Double.POSITIVE_INFINITY
import java.lang.Double.NEGATIVE_INFINITY

class Point3D(x: Double, y: Double, z: Double) : Element3D(x, y, z) {

    constructor(e: Element3D) : this(e.x, e.y, e.z) {}

    fun negate() = Point3D(-x, -y, -z)

    operator fun plus(v: Vector3D) = Point3D(x + v.x, y + v.y, z + v.z)

    operator fun plus(f: Double) = Point3D(x + f, y + f, z + f)

    operator fun minus(v: Point3D): Vector3D = Vector3D(x - v.x, y - v.y, z - v.z)

    operator fun minus(v: Vector3D) = Point3D(x - v.x, y - v.y, z - v.z)

    operator fun minus(f: Double) = Point3D(x - f, y - f, z - f)

    companion object {
        val ORIGIN = Point3D(0.0, 0.0, 0.0)
        val MAX = Point3D(POSITIVE_INFINITY, POSITIVE_INFINITY, POSITIVE_INFINITY)
        val MIN = Point3D(NEGATIVE_INFINITY, NEGATIVE_INFINITY, NEGATIVE_INFINITY)
        val DEFAULT_CAMERA = Point3D(0.0, 10.0, 10.0)
    }

}
