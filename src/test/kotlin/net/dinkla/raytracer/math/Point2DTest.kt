package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class Point2DTest {

    private val p = Point2D(1.0, 2.0)

    @Test
    fun negate() {
        val v = Vector2D(-1.0, -2.0)
        assertEquals(v, p.negate())
    }

    @Test
    fun `add a vector`() {
        val v = Vector2D(-1.0, -2.0)
        assertEquals(Point2D.ORIGIN, p + v)
    }

    @Test
    fun `subtract a vector`() {
        val v = Vector2D(1.0, 2.0)
        assertEquals(Point2D.ORIGIN, p - v)
    }
}