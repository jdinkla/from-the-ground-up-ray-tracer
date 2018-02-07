package net.dinkla.raytracer.math

import java.lang.Math.sqrt

open class Element3D {

    val x: Double
    val y: Double
    val z: Double

    constructor(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor(e: Element3D) {
        this.x = e.x
        this.y = e.y
        this.z = e.z
    }

    fun sqrLength(): Double {
        return x * x + y * y + z * z
    }

    fun length(): Double {
        return sqrt(sqrLength())
    }

    fun distanceSquared(p: Element3D): Double {
        val dx = x - p.x
        val dy = y - p.y
        val dz = z - p.z
        return dx * dx + dy * dy + dz * dz
    }

    fun ith(axis: Axis): Double {
        when (axis) {
            Axis.X -> return x
            Axis.Y -> return y
            Axis.Z -> return z
            else -> return z
        }
    }

    override fun equals(other: Any?): Boolean {
        if (null != other) {
            if (other is Element3D) {
                val e = other as Element3D?
                return x == e!!.x && y == e.y && z == e.z
            }
        }
        return false
    }

    override fun toString(): String {
        return "($x,$y,$z)"
    }

}
