package net.dinkla.raytracer.math

open class Point2D(x: Double, y: Double) : Element2D(x, y) {

    operator fun unaryMinus(): Point2D {
        return Point2D(-x, -y);
    }

    operator fun plus(v: Vector2D): Point2D {
        return Point2D(x + v.x, y + v.y)
    }

    operator fun minus(v: Vector2D): Point2D {
        return Point2D(x - v.x, y - v.y)
    }

}