package net.dinkla.raytracer.math

class Vector2D : Element2D {

    constructor(x: Double, y: Double) : super(x, y) {}

    operator fun plus(v: Vector2D): Vector2D {
        return Vector2D(x + v.x, y + v.y)
    }

    operator fun minus(v: Vector2D): Vector2D {
        return Vector2D(x - v.x, y - v.y)
    }

    fun mult(s: Double): Vector2D {
        return Vector2D(s * x, s * y)
    }

    fun dot(v: Vector2D): Double {
        return x * v.x + y * v.y
    }

    fun dot(v: Normal): Double {
        return x * v.x + y * v.y
    }

    fun normalize(): Vector2D {
        val l = length()
        return Vector2D(x / l, y / l)
    }

}