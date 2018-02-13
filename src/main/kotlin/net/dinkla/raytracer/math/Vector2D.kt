package net.dinkla.raytracer.math

class Vector2D(x: Double, y: Double) : Element2D(x, y) {

    operator fun plus(v: Vector2D) = Vector2D(x + v.x, y + v.y)

    operator fun minus(v: Vector2D) = Vector2D(x - v.x, y - v.y)

    operator fun times(s: Double) = Vector2D(s * x, s * y)

    infix fun dot(v: Vector2D): Double = x * v.x + y * v.y

    infix fun dot(v: Normal): Double = x * v.x + y * v.y

    fun normalize(): Vector2D {
        val l = length()
        return Vector2D(x / l, y / l)
    }

}

operator fun Double.times(v: Vector2D) = Vector2D(this * v.x, this * v.y)
