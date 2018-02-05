package net.dinkla.raytracer.math

class Vector2D : Element2D {

    constructor(x: Float, y: Float) : super(x, y) {}

    constructor(x: Double, y: Double) : super(x, y) {}

    operator fun plus(v: Vector2D): Vector2D {
        return Vector2D(x + v.x, y + v.y)
    }

    operator fun minus(v: Vector2D): Vector2D {
        return Vector2D(x - v.x, y - v.y)
    }

    fun mult(s: Float): Vector2D {
        return Vector2D(s * x, s * y)
    }

    fun dot(v: Vector2D): Float {
        return x * v.x + y * v.y
    }

    fun dot(v: Normal): Float {
        return x * v.x + y * v.y
    }

    fun normalize(): Vector2D {
        val l = length()
        return Vector2D(x / l, y / l)
    }

}