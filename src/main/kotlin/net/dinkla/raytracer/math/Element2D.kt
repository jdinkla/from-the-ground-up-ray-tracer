package net.dinkla.raytracer.math

import java.util.Objects
import kotlin.math.sqrt

open class Element2D(val x: Double, val y: Double) {

    fun sqrLength(): Double = x * x + y * y

    fun length(): Double = sqrt(sqrLength())

    override fun equals(other: Any?): Boolean {
        return if (null == other || other !is Element2D) {
            false
        } else {
            val e = other as Element2D?
            x == e!!.x && y == e.y
        }
    }

    override fun hashCode(): Int = Objects.hash(x, y)

    override fun toString(): String = "($x,$y)"

}