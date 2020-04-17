package net.dinkla.raytracer.math

import net.dinkla.raytracer.utilities.hash
import java.lang.Math.sqrt

open class Element3D(val x: Double, val y: Double, val z: Double) {

    constructor(e: Element3D) : this(e.x, e.y, e.z)

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
        val p: Element3D = other as? Element3D ?: return false
        return x == p.x && y == p.y && z == p.z
    }

    override fun hashCode(): Int = hash(x, y, z)

    override fun toString(): String = "($x,$y,$z)"

}
