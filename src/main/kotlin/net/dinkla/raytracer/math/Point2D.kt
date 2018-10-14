package net.dinkla.raytracer.math

class Point2D(x: Double, y: Double) : Element2D(x, y) {

     operator fun unaryMinus() = Vector2D(-x, -y)

    operator fun plus(v: Vector2D) = Point2D(x + v.x, y + v.y)

    operator fun minus(v: Vector2D) = Point2D(x - v.x, y - v.y)

    companion object {
        val ORIGIN = Point2D(0.0, 0.0)
    }
}