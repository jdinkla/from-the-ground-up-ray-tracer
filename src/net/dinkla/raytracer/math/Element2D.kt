package net.dinkla.raytracer.math

import java.lang.StrictMath.sqrt

open class Element2D {

    val x: Double
    val y: Double

    constructor(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    fun sqrLength(): Double {
        return x * x + y * y
    }

    fun length(): Double {
        return sqrt(sqrLength())
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Element2D) {
            return false
        } else {
            val e = other as Element2D?
            return x == e!!.x && y == e.y
        }
    }

    override fun toString(): String {
        return "($x,$y)"
    }

}