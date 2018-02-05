package net.dinkla.raytracer.math

import java.lang.StrictMath.sqrt

open class Element2D {

    val x: Float
    val y: Float

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    constructor(x: Double, y: Double) {
        this.x = x.toFloat()
        this.y = y.toFloat()
    }

    fun sqrLength(): Float {
        return x * x + y * y
    }

    fun length(): Float {
        return sqrt(sqrLength().toDouble()).toFloat()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is Element2D) {
            return false
        } else {
            val e = obj as Element2D?
            return x == e!!.x && y == e.y
        }
    }

    override fun toString(): String {
        return "($x,$y)"
    }


}