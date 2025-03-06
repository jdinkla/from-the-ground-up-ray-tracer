package net.dinkla.raytracer.math

import kotlin.math.sqrt

data class Vector2D(
    val x: Double,
    val y: Double,
) {
    operator fun plus(v: Vector2D) = Vector2D(x + v.x, y + v.y)

    operator fun minus(v: Vector2D) = Vector2D(x - v.x, y - v.y)

    operator fun times(s: Double) = Vector2D(s * x, s * y)

    infix fun dot(v: Vector2D): Double = x * v.x + y * v.y

    infix fun dot(v: Normal): Double = x * v.x + y * v.y

    operator fun unaryMinus() = Vector2D(-x, -y)

    val length: Double
        get() = sqrt(x * x + y * y)

    fun normalize(): Vector2D {
        val len = length
        return Vector2D(x / len, y / len)
    }

    override fun toString(): String = "($x,$y)"
}

operator fun Double.times(v: Vector2D) = Vector2D(this * v.x, this * v.y)
