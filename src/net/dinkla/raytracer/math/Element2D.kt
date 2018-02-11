package net.dinkla.raytracer.math

import java.lang.StrictMath.sqrt

open class Element2D(val x: Double, val y: Double) {

    fun sqrLength(): Double = x * x + y * y

    fun length(): Double = sqrt(sqrLength())

    override fun equals(other: Any?): Boolean {
        if (other !is Element2D) {
            return false
        } else {
            val e = other as Element2D?
            return x == e!!.x && y == e.y
        }
    }

    override fun toString(): String = "($x,$y)"

}