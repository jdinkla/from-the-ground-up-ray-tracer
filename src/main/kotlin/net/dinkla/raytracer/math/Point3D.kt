package net.dinkla.raytracer.math

import java.lang.Double.NEGATIVE_INFINITY
import java.lang.Double.POSITIVE_INFINITY

class Point3D(x: Double, y: Double, z: Double) : Element3D(x, y, z) {

    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())

    constructor(e: Element3D) : this(e.x, e.y, e.z)

    operator fun unaryMinus() = Vector3D(-x, -y, -z)

    operator fun plus(v: Vector3D) = Point3D(x + v.x, y + v.y, z + v.z)

    operator fun plus(f: Double) = Point3D(x + f, y + f, z + f)

    operator fun minus(v: Point3D): Vector3D = Vector3D(x - v.x, y - v.y, z - v.z)

    operator fun minus(v: Vector3D) = Point3D(x - v.x, y - v.y, z - v.z)

    operator fun minus(f: Double) = Point3D(x - f, y - f, z - f)

    override fun equals(other: Any?): Boolean {
        return if (null == other || other !is Point3D) {
            false
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int = super.hashCode()

    companion object {
        val UNIT = Point3D(1.0, 1.0, 1.0)
        val ORIGIN = Point3D(0, 0, 0)
        val MAX = Point3D(POSITIVE_INFINITY, POSITIVE_INFINITY, POSITIVE_INFINITY)
        val MIN = Point3D(NEGATIVE_INFINITY, NEGATIVE_INFINITY, NEGATIVE_INFINITY)
    }

}
