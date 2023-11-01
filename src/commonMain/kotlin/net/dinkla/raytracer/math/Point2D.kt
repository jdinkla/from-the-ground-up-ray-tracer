package net.dinkla.raytracer.math

import kotlin.math.sqrt

data class Point2D(val x: Double, val y: Double) {

    operator fun unaryMinus() = Vector2D(-x, -y)

    operator fun plus(v: Vector2D) = Point2D(x + v.x, y + v.y)

    operator fun minus(v: Vector2D) = Point2D(x - v.x, y - v.y)

    val length: Double
        get() = sqrt(x * x + y * y)

    override fun toString(): String = "($x,$y)"

    companion object {
        val ORIGIN = Point2D(0.0, 0.0)
    }
}
