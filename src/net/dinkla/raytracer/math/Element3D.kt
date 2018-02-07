package net.dinkla.raytracer.math

import java.lang.Math.sqrt

open class Element3D {

    val x: Float
    val y: Float
    val z: Float

    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor(x: Double, y: Double, z: Double) {
        this.x = x.toFloat()
        this.y = y.toFloat()
        this.z = z.toFloat()
    }

    constructor(e: Element3D) {
        this.x = e.x
        this.y = e.y
        this.z = e.z
    }

    fun sqrLength(): Float {
        return x * x + y * y + z * z
    }

    fun length(): Float {
        return sqrt(sqrLength().toDouble()).toFloat()
    }

    fun distanceSquared(p: Element3D): Float {
        val dx = x - p.x
        val dy = y - p.y
        val dz = z - p.z
        return dx * dx + dy * dy + dz * dz
    }

    fun ith(axis: Axis): Float {
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
