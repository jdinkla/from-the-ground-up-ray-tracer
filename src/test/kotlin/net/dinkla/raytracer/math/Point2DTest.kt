package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class Point2DTest {

    private val p = Point2D(1.0, 2.0)

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

    @Test
    fun equals() {
        assertEquals(p, Point2D(1.0, 2.0))
        assertNotEquals(p, Point2D(1.0, 1.98))
        assertNotEquals(p, Point2D(0.98, 2.0))
        assertNotEquals(p, 3.0)
        assertNotEquals(p, null)
    }

    @Test
    fun unaryMinus() {
        assertEquals(-p, Point2D(-1.0, -2.0))
    }
}