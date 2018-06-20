package net.dinkla.raytracer.math

import java.lang.Math.sqrt

open class Element3D(val x: Double, val y: Double, val z: Double) {

    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble()) {}

    constructor(e: Element3D) : this(e.x, e.y, e.z) {}

    fun sqrLength(): Double = x * x + y * y + z * z

    fun length(): Double = sqrt(sqrLength())

    fun sqrDistance(p: Element3D): Double {
        val dx = x - p.x
        val dy = y - p.y
        val dz = z - p.z
        return dx * dx + dy * dy + dz * dz
    }

    fun ith(axis: Axis) = when (axis) {
        Axis.X -> x
        Axis.Y -> y
        Axis.Z -> z
    }

    override fun equals(other: Any?): Boolean {
        if (null == other || other !is Element3D) {
            return false
        } else {
            return x == other.x && y == other.y && z == other.z
        }
    }

    override fun hashCode(): Int = listOf(x, y, z).hashCode()

    override fun toString(): String = "($x,$y,$z)"

}
